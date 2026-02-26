<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Admin\AdminDashboardController;
use App\Http\Controllers\Admin\HalalProductController;

/*
|--------------------------------------------------------------------------
| Admin Web Routes
|--------------------------------------------------------------------------
*/

Route::prefix('admin')->middleware(['auth', 'admin'])->name('admin.')->group(function () {
    // Dashboard
    Route::get('/', [AdminDashboardController::class, 'index'])->name('dashboard');
    
    // Dashboard API endpoints
    Route::get('/api/products-needing-recheck', [AdminDashboardController::class, 'getProductsNeedingRecheck']);
    Route::post('/api/recheck-products', [AdminDashboardController::class, 'recheckProducts']);
    Route::get('/api/export-data', [AdminDashboardController::class, 'exportData']);
    Route::get('/api/system-health', [AdminDashboardController::class, 'systemHealth']);
    
    // Halal Products Management
    Route::resource('halal-products', HalalProductController::class);
    Route::post('halal-products/search', [HalalProductController::class, 'search'])->name('halal-products.search');
    Route::post('halal-products/verify', [HalalProductController::class, 'verify'])->name('halal-products.verify');
    Route::post('halal-products/batch-verify', [HalalProductController::class, 'batchVerify'])->name('halal-products.batch-verify');
    Route::put('halal-products/{id}', [HalalProductController::class, 'manualUpdate'])->name('halal-products.update');
    Route::delete('halal-products/{id}', [HalalProductController::class, 'destroy'])->name('halal-products.destroy');
    Route::post('halal-products/recheck-old', [HalalProductController::class, 'recheckOld'])->name('halal-products.recheck');
    Route::get('halal-products/export', [HalalProductController::class, 'export'])->name('halal-products.export');
});
