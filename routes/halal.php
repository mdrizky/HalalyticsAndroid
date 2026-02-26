<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Admin\HalalProductController;
use App\Http\Controllers\Api\ProductController;

/*
|--------------------------------------------------------------------------
| Web Routes - Admin Panel
|--------------------------------------------------------------------------
*/

Route::prefix('admin')->middleware(['auth', 'admin'])->group(function () {
    // Halal Products Management
    Route::get('/halal-products', [HalalProductController::class, 'index'])->name('admin.halal-products.index');
    Route::get('/halal-products/{id}', [HalalProductController::class, 'show'])->name('admin.halal-products.show');
    Route::post('/halal-products/search', [HalalProductController::class, 'search'])->name('admin.halal-products.search');
    Route::post('/halal-products/verify', [HalalProductController::class, 'verify'])->name('admin.halal-products.verify');
    Route::post('/halal-products/batch-verify', [HalalProductController::class, 'batchVerify'])->name('admin.halal-products.batch-verify');
    Route::put('/halal-products/{id}', [HalalProductController::class, 'manualUpdate'])->name('admin.halal-products.update');
    Route::delete('/halal-products/{id}', [HalalProductController::class, 'destroy'])->name('admin.halal-products.destroy');
    Route::post('/halal-products/recheck-old', [HalalProductController::class, 'recheckOld'])->name('admin.halal-products.recheck');
    Route::get('/halal-products/export', [HalalProductController::class, 'export'])->name('admin.halal-products.export');
});

/*
|--------------------------------------------------------------------------
| API Routes - Mobile App
|--------------------------------------------------------------------------
*/

Route::prefix('api/v1')->group(function () {
    // Public routes
    Route::get('/products/{barcode}', [ProductController::class, 'show']);
    Route::post('/products/check-halal', [ProductController::class, 'checkHalal']);
    Route::post('/products/batch-check-halal', [ProductController::class, 'batchCheckHalal']);
    Route::get('/products/search-halal', [ProductController::class, 'searchHalal']);
    Route::get('/halal-stats', [ProductController::class, 'getHalalStats']);
    Route::get('/recent-checks', [ProductController::class, 'getRecentChecks']);
    
    // Protected routes (if needed)
    Route::middleware('auth:sanctum')->group(function () {
        Route::post('/products/report-non-halal', [ProductController::class, 'reportNonHalal']);
    });
});
