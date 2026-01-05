<?php

use Illuminate\Support\Facades\Route;
use App\Http\Controllers\ApiController;
use App\Http\Controllers\ProductExternalController;

// ==========================================================
// PUBLIC ROUTES (tanpa login)
// ==========================================================
Route::post('/login', [ApiController::class, 'login']);
Route::post('/register', [ApiController::class, 'register']);
Route::get('/products', [ApiController::class, 'indexProduct']);
Route::get('/products/{id}', [ApiController::class, 'showProduct']);
Route::get('/kategori', [ApiController::class, 'indexKategori']);
Route::get('/search/product', [ApiController::class, 'searchProduct']);
Route::get('/scan/barcode/{barcode}', [ApiController::class, 'scanProductByBarcode']);

// ==========================================================
// 🌍 PRODUK INTERNASIONAL (OPENFOODFACTS) - PUBLIC
// ==========================================================
Route::get('/products/search', [ProductExternalController::class, 'search']);
Route::get('/products/{barcode}', [ProductExternalController::class, 'detail']);
Route::get('/products/halal', [ProductExternalController::class, 'halal']);
Route::get('/products/vegetarian', [ProductExternalController::class, 'vegetarian']);
Route::get('/products/vegan', [ProductExternalController::class, 'vegan']);
Route::get('/products/brand/{brand}', [ProductExternalController::class, 'brand']);
Route::get('/products/category/{category}', [ProductExternalController::class, 'category']);

// ==========================================================
// PROTECTED ROUTES (butuh token login / Sanctum)
// ==========================================================
Route::middleware('auth:sanctum')->group(function () {
    // PROFILE
    Route::get('/profile', [ApiController::class, 'profile']);
    Route::post('/update-profile', [ApiController::class, 'updateProfile']);
    Route::post('/logout', [ApiController::class, 'logout']);

    // SCANS
    Route::post('/scans', [ApiController::class, 'storeScan']);
    Route::get('/my-scans', [ApiController::class, 'indexMyScans']);

    // REPORTS
    Route::post('/reports', [ApiController::class, 'storeReport']);
    Route::get('/my-reports', [ApiController::class, 'indexMyReports']);
});
