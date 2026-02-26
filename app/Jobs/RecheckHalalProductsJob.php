<?php

namespace App\Jobs;

use App\Services\HalalCertificationService;
use Illuminate\Bus\Queueable;
use Illuminate\Contracts\Queue\ShouldQueue;
use Illuminate\Foundation\Bus\Dispatchable;
use Illuminate\Queue\InteractsWithQueue;
use Illuminate\Queue\SerializesModels;

class RecheckHalalProductsJob implements ShouldQueue
{
    use Dispatchable, InteractsWithQueue, Queueable, SerializesModels;

    protected $halalService;

    public function __construct()
    {
        $this->halalService = app(HalalCertificationService::class);
    }

    public function handle()
    {
        // Get products that need rechecking
        $products = \App\Models\HalalProduct::where('last_checked_at', '<', now()->subDays(30))
            ->orWhere('halal_status', 'unknown')
            ->limit(100)
            ->get();

        foreach ($products as $product) {
            try {
                $this->halalService->verifyAndStore(
                    $product->product_barcode,
                    $product->product_name,
                    $product->brand
                );
                
                // Delay between requests to avoid rate limiting
                usleep(500000); // 0.5 second delay
                
            } catch (\Exception $e) {
                \Log::error("Failed to recheck product {$product->product_barcode}: " . $e->getMessage());
                continue;
            }
        }

        \Log::info("Rechecked {$products->count()} halal products");
    }
}
