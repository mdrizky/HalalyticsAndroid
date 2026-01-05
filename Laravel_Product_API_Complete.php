<?php

// 🌍 COMPLETE LARAVEL PRODUCT API - COPY PASTE SAJA!
// Simpan sebagai: app/Http/Controllers/ProductExternalController.php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Http;
use Illuminate\Support\Facades\Cache;

class ProductExternalController extends Controller
{
    private $base = "https://world.openfoodfacts.org";
    private $cacheDuration = 3600;

    // 🔍 Search Produk Global
    public function search(Request $request)
    {
        $query = $request->input('q');
        
        if (!$query) {
            return response()->json([
                'products' => [],
                'message' => 'Query required'
            ]);
        }

        $cacheKey = "search_".md5($query);

        $result = Cache::remember($cacheKey, $this->cacheDuration, function() use ($query) {
            $response = Http::get($this->base."/cgi/search.pl", [
                'search_terms' => $query,
                'search_simple' => 1,
                'json' => 1,
                'page_size' => 20,
            ]);

            return $response->json();
        });

        return response()->json($result);
    }

    // 📦 Detail Produk (Barcode)
    public function detail($barcode)
    {
        $cacheKey = "detail_".$barcode;

        $result = Cache::remember($cacheKey, $this->cacheDuration, function() use ($barcode) {
            $response = Http::get($this->base."/api/v2/product/{$barcode}.json");
            return $response->json();
        });

        return response()->json($result);
    }

    // 🕌 Produk Halal
    public function halal(Request $request)
    {
        $query = $request->input('q', '');
        $cacheKey = "halal_".md5($query);

        $result = Cache::remember($cacheKey, $this->cacheDuration, function() use ($query) {
            $params = [
                'search_terms' => $query,
                'tagtype_0' => 'labels',
                'tag_contains_0' => 'contains',
                'tag_0' => 'en:halal',
                'json' => 1,
                'page_size' => 20
            ];

            $response = Http::get($this->base."/cgi/search.pl", $params);
            return $response->json();
        });

        return response()->json($result);
    }

    // 🌱 Vegetarian
    public function vegetarian(Request $request)
    {
        $query = $request->input('q', '');
        $cacheKey = "vegetarian_".md5($query);

        $result = Cache::remember($cacheKey, $this->cacheDuration, function() use ($query) {
            $params = [
                'search_terms' => $query,
                'tagtype_0' => 'labels',
                'tag_contains_0' => 'contains',
                'tag_0' => 'en:vegetarian',
                'json' => 1,
                'page_size' => 20
            ];

            $response = Http::get($this->base."/cgi/search.pl", $params);
            return $response->json();
        });

        return response()->json($result);
    }

    // 🌱 Vegan
    public function vegan(Request $request)
    {
        $query = $request->input('q', '');
        $cacheKey = "vegan_".md5($query);

        $result = Cache::remember($cacheKey, $this->cacheDuration, function() use ($query) {
            $params = [
                'search_terms' => $query,
                'tagtype_0' => 'labels',
                'tag_contains_0' => 'contains',
                'tag_0' => 'en:vegan',
                'json' => 1,
                'page_size' => 20
            ];

            $response = Http::get($this->base."/cgi/search.pl", $params);
            return $response->json();
        });

        return response()->json($result);
    }

    // 🏢 Brand search
    public function brand($brand, Request $request)
    {
        $cacheKey = "brand_".md5($brand);

        $result = Cache::remember($cacheKey, $this->cacheDuration, function() use ($brand) {
            $params = [
                'tagtype_0' => 'brands',
                'tag_contains_0' => 'contains',
                'tag_0' => $brand,
                'json' => 1,
                'page_size' => 20
            ];

            $response = Http::get($this->base."/cgi/search.pl", $params);
            return $response->json();
        });

        return response()->json($result);
    }

    // 🥗 Category search
    public function category($category, Request $request)
    {
        $cacheKey = "category_".md5($category);

        $result = Cache::remember($cacheKey, $this->cacheDuration, function() use ($category) {
            $params = [
                'tagtype_0' => 'categories',
                'tag_contains_0' => 'contains',
                'tag_0' => $category,
                'json' => 1,
                'page_size' => 20
            ];

            $response = Http::get($this->base."/cgi/search.pl", $params);
            return $response->json();
        });

        return response()->json($result);
    }
}

/*
==========================================================
🚀 STEP BY STEP SETUP - JALANKAN DI TERMINAL:
==========================================================

1. Install Guzzle HTTP Client:
   composer require guzzlehttp/guzzle

2. Copy file ini ke Laravel:
   cp Laravel_Product_API_Complete.php /path/to/laravel/app/Http/Controllers/ProductExternalController.php

3. Update routes/api.php (tambahkan ini):
   
   use App\Http\Controllers\ProductExternalController;

   // 🌍 PRODUK INTERNASIONAL (OPENFOODFACTS) - PUBLIC
   Route::get('/products/search', [ProductExternalController::class, 'search']);
   Route::get('/products/{barcode}', [ProductExternalController::class, 'detail']);
   Route::get('/products/halal', [ProductExternalController::class, 'halal']);
   Route::get('/products/vegetarian', [ProductExternalController::class, 'vegetarian']);
   Route::get('/products/vegan', [ProductExternalController::class, 'vegan']);
   Route::get('/products/brand/{brand}', [ProductExternalController::class, 'brand']);
   Route::get('/products/category/{category}', [ProductExternalController::class, 'category']);

4. Clear cache:
   php artisan cache:clear
   php artisan config:clear
   php artisan route:clear

5. Start Laravel server:
   php artisan serve --host=0.0.0.0 --port=8000

6. Test API (buka di browser):
   http://localhost:8000/api/products/search?q=coca%20cola
   http://localhost:8000/api/products/5449000131805
   http://localhost:8000/api/products/halal?q=snack

==========================================================
📱 ANDROID CONNECTION:
==========================================================

Pastikan base URL di Android:
private const val BASE_URL = "http://10.0.2.2:8000/api/"

==========================================================
✅ EXPECTED RESULTS:
==========================================================

Android app akan menampilkan:
- Real search results dari OpenFoodFacts
- Product images dan informasi lengkap  
- Nutriscore badges A-E dengan warna
- Product detail dengan ingredients
- Filter by Halal/Vegetarian/Vegan

==========================================================
*/
