<?php

namespace App\Services;

use Illuminate\Support\Facades\Http;
use Illuminate\Support\Facades\Cache;
use Illuminate\Support\Facades\Log;
use App\Models\HalalProduct;

class HalalCertificationService
{
    private $muiApiUrl;
    private $muiApiKey;

    public function __construct()
    {
        $this->muiApiUrl = config('services.mui.api_url');
        $this->muiApiKey = config('services.mui.api_key');
    }

    /**
     * Cek produk dari database MUI
     */
    public function checkMUIDatabase($productName, $brand = null)
    {
        try {
            // Contoh integrasi dengan API MUI (sesuaikan dengan API actual)
            $response = Http::withHeaders([
                'Authorization' => 'Bearer ' . $this->muiApiKey,
                'Accept' => 'application/json'
            ])->get($this->muiApiUrl . '/products/search', [
                'name' => $productName,
                'brand' => $brand
            ]);

            if ($response->successful()) {
                return [
                    'success' => true,
                    'data' => $response->json()
                ];
            }

            return [
                'success' => false,
                'message' => 'Product not found in MUI database'
            ];

        } catch (\Exception $e) {
            Log::error('MUI API Error: ' . $e->getMessage());
            return [
                'success' => false,
                'message' => $e->getMessage()
            ];
        }
    }

    /**
     * Scraping dari website halal MUI (jika tidak ada API)
     */
    public function scrapeMUIWebsite($productName)
    {
        try {
            // URL pencarian produk halal MUI
            $url = "https://www.halalmui.org/mui14/index.php/main/produk_halal_cari";
            
            $response = Http::asForm()->post($url, [
                'nama_produk' => $productName,
                'page' => 1
            ]);

            if ($response->successful()) {
                $html = $response->body();
                
                // Parse HTML response
                $dom = new \DOMDocument();
                @$dom->loadHTML($html);
                $xpath = new \DOMXPath($dom);
                
                $products = [];
                
                // Extract data dari table hasil pencarian
                $rows = $xpath->query("//table[@class='table']/tbody/tr");
                
                foreach ($rows as $row) {
                    $cols = $xpath->query("./td", $row);
                    if ($cols->length >= 4) {
                        $products[] = [
                            'name' => trim($cols->item(1)->textContent),
                            'brand' => trim($cols->item(2)->textContent),
                            'certificate_number' => trim($cols->item(3)->textContent),
                            'valid_until' => trim($cols->item(4)->textContent),
                        ];
                    }
                }
                
                return [
                    'success' => true,
                    'data' => $products
                ];
            }

        } catch (\Exception $e) {
            Log::error('MUI Scraping Error: ' . $e->getMessage());
        }

        return [
            'success' => false,
            'message' => 'Failed to scrape MUI website'
        ];
    }

    /**
     * Cek dan simpan ke database lokal
     */
    public function verifyAndStore($barcode, $productName, $brand)
    {
        // Cek di database lokal dulu (cache 30 hari)
        $existing = HalalProduct::where('product_barcode', $barcode)
            ->where('last_checked_at', '>', now()->subDays(30))
            ->first();

        if ($existing) {
            return [
                'success' => true,
                'data' => $existing,
                'source' => 'cache'
            ];
        }

        // Coba API MUI dulu
        $muiResult = $this->checkMUIDatabase($productName, $brand);

        if ($muiResult['success'] && !empty($muiResult['data'])) {
            $productData = $muiResult['data'][0] ?? $muiResult['data'];

            // Simpan atau update
            $halalProduct = HalalProduct::updateOrCreate(
                ['product_barcode' => $barcode],
                [
                    'product_name' => $productName,
                    'brand' => $brand,
                    'halal_certificate_number' => $productData['certificate_number'] ?? null,
                    'halal_status' => 'halal',
                    'certification_body' => 'MUI',
                    'certificate_valid_until' => $productData['valid_until'] ?? null,
                    'certificate_data' => $productData,
                    'last_checked_at' => now()
                ]
            );

            return [
                'success' => true,
                'data' => $halalProduct,
                'source' => 'mui_api'
            ];
        }

        // Jika API gagal, coba scraping
        $scrapeResult = $this->scrapeMUIWebsite($productName);

        if ($scrapeResult['success'] && !empty($scrapeResult['data'])) {
            $productData = $scrapeResult['data'][0];

            $halalProduct = HalalProduct::updateOrCreate(
                ['product_barcode' => $barcode],
                [
                    'product_name' => $productName,
                    'brand' => $brand,
                    'halal_certificate_number' => $productData['certificate_number'] ?? null,
                    'halal_status' => 'halal',
                    'certification_body' => 'MUI',
                    'certificate_valid_until' => $productData['valid_until'] ?? null,
                    'certificate_data' => $productData,
                    'last_checked_at' => now()
                ]
            );

            return [
                'success' => true,
                'data' => $halalProduct,
                'source' => 'mui_scraping'
            ];
        }

        // Jika tidak ditemukan, tandai sebagai unknown
        $halalProduct = HalalProduct::updateOrCreate(
            ['product_barcode' => $barcode],
            [
                'product_name' => $productName,
                'brand' => $brand,
                'halal_status' => 'unknown',
                'last_checked_at' => now()
            ]
        );

        return [
            'success' => true,
            'data' => $halalProduct,
            'source' => 'not_found'
        ];
    }

    /**
     * Batch check untuk multiple products
     */
    public function batchVerify($products)
    {
        $results = [];
        
        foreach ($products as $product) {
            $result = $this->verifyAndStore(
                $product['barcode'],
                $product['name'],
                $product['brand'] ?? ''
            );
            
            $results[] = [
                'barcode' => $product['barcode'],
                'status' => $result['data']->halal_status,
                'certificate' => $result['data']->halal_certificate_number,
                'source' => $result['source']
            ];
        }
        
        return $results;
    }

    /**
     * Re-check existing products (background job)
     */
    public function recheckOldProducts()
    {
        $products = HalalProduct::where('last_checked_at', '<', now()->subDays(30))
            ->orWhere('halal_status', 'unknown')
            ->limit(100)
            ->get();

        foreach ($products as $product) {
            $this->verifyAndStore(
                $product->product_barcode,
                $product->product_name,
                $product->brand
            );
        }
    }
}
