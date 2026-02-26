<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Product;
use App\Models\HalalProduct;
use App\Services\HalalCertificationService;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Cache;

class ProductController extends Controller
{
    protected $halalService;

    public function __construct(HalalCertificationService $halalService)
    {
        $this->halalService = $halalService;
    }

    /**
     * Get product dengan info halal
     */
    public function show($barcode)
    {
        // Cache untuk performance
        $cacheKey = "product_with_halal_$barcode";
        
        $result = Cache::remember($cacheKey, 300, function() use ($barcode) {
            // Ambil produk dari database internasional
            $product = Product::where('barcode', $barcode)->first();

            if (!$product) {
                return [
                    'success' => false,
                    'message' => 'Product not found'
                ];
            }

            // Cek status halal
            $halalInfo = $this->halalService->verifyAndStore(
                $barcode,
                $product->name,
                $product->brand
            );

            return [
                'success' => true,
                'data' => [
                    'product' => $product,
                    'halal_info' => $halalInfo['data'],
                    'halal_source' => $halalInfo['source']
                ]
            ];
        });

        return response()->json($result);
    }

    /**
     * Check halal status only
     */
    public function checkHalal(Request $request)
    {
        $request->validate([
            'barcode' => 'required|string',
            'product_name' => 'required|string',
            'brand' => 'nullable|string'
        ]);

        $result = $this->halalService->verifyAndStore(
            $request->barcode,
            $request->product_name,
            $request->brand
        );

        return response()->json([
            'success' => true,
            'data' => [
                'halal_status' => $result['data']->halal_status,
                'certificate_number' => $result['data']->halal_certificate_number,
                'certification_body' => $result['data']->certification_body,
                'valid_until' => $result['data']->certificate_valid_until,
                'last_checked' => $result['data']->last_checked_at,
                'source' => $result['source'],
                'is_certificate_valid' => $result['data']->isCertificateValid()
            ]
        ]);
    }

    /**
     * Batch check multiple products
     */
    public function batchCheckHalal(Request $request)
    {
        $request->validate([
            'products' => 'required|array',
            'products.*.barcode' => 'required|string',
            'products.*.name' => 'required|string',
            'products.*.brand' => 'nullable|string'
        ]);

        $results = $this->halalService->batchVerify($request->products);

        return response()->json([
            'success' => true,
            'data' => $results
        ]);
    }

    /**
     * Search halal products
     */
    public function searchHalal(Request $request)
    {
        $request->validate([
            'query' => 'required|string|min:3',
            'status' => 'nullable|in:halal,non_halal,unknown'
        ]);

        $query = $request->query;
        $status = $request->status;

        $products = HalalProduct::with('product')
            ->where(function($q) use ($query) {
                $q->where('product_name', 'LIKE', "%{$query}%")
                  ->orWhere('brand', 'LIKE', "%{$query}%")
                  ->orWhere('product_barcode', 'LIKE', "%{$query}%");
            })
            ->when($status, function($q) use ($status) {
                $q->where('halal_status', $status);
            })
            ->limit(20)
            ->get();

        return response()->json([
            'success' => true,
            'data' => $products
        ]);
    }

    /**
     * Get halal statistics
     */
    public function getHalalStats()
    {
        $cacheKey = 'halal_stats';
        
        $stats = Cache::remember($cacheKey, 3600, function() {
            return [
                'total_products' => HalalProduct::count(),
                'halal_products' => HalalProduct::halal()->count(),
                'non_halal_products' => HalalProduct::nonHalal()->count(),
                'unknown_products' => HalalProduct::unknown()->count(),
                'certified_by_mui' => HalalProduct::where('certification_body', 'MUI')->count(),
                'valid_certificates' => HalalProduct::whereNotNull('certificate_valid_until')
                    ->where('certificate_valid_until', '>', now())
                    ->count(),
                'last_updated' => HalalProduct::latest('last_checked_at')->value('last_checked_at')
            ];
        });

        return response()->json([
            'success' => true,
            'data' => $stats
        ]);
    }

    /**
     * Get recent halal checks
     */
    public function getRecentChecks()
    {
        $recent = HalalProduct::with('product')
            ->latest('last_checked_at')
            ->limit(10)
            ->get();

        return response()->json([
            'success' => true,
            'data' => $recent
        ]);
    }

    /**
     * Report product as non-halal
     */
    public function reportNonHalal(Request $request)
    {
        $request->validate([
            'barcode' => 'required|string',
            'reason' => 'required|string',
            'evidence' => 'nullable|string'
        ]);

        $product = HalalProduct::where('product_barcode', $request->barcode)->first();

        if (!$product) {
            return response()->json([
                'success' => false,
                'message' => 'Product not found'
            ], 404);
        }

        // Update status ke non-halal
        $product->update([
            'halal_status' => 'non_halal',
            'certificate_data' => array_merge($product->certificate_data ?? [], [
                'report_reason' => $request->reason,
                'report_evidence' => $request->evidence,
                'reported_at' => now()
            ])
        ]);

        return response()->json([
            'success' => true,
            'message' => 'Product reported as non-halal',
            'data' => $product
        ]);
    }
}
