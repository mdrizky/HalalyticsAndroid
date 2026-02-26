<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\HalalProduct;
use App\Models\Product;
use App\Services\HalalCertificationService;
use Illuminate\Http\Request;

class HalalProductController extends Controller
{
    protected $halalService;

    public function __construct(HalalCertificationService $halalService)
    {
        $this->halalService = $halalService;
    }

    /**
     * Display all halal products
     */
    public function index()
    {
        $products = HalalProduct::with('product')
            ->latest()
            ->paginate(20);
            
        $stats = [
            'total' => HalalProduct::count(),
            'halal' => HalalProduct::halal()->count(),
            'non_halal' => HalalProduct::nonHalal()->count(),
            'unknown' => HalalProduct::unknown()->count(),
        ];

        return view('admin.halal-products.index', compact('products', 'stats'));
    }

    /**
     * Search product from MUI database
     */
    public function search(Request $request)
    {
        $request->validate([
            'product_name' => 'required|string',
            'brand' => 'nullable|string'
        ]);

        $result = $this->halalService->checkMUIDatabase(
            $request->product_name,
            $request->brand
        );

        return response()->json($result);
    }

    /**
     * Verify product halal status
     */
    public function verify(Request $request)
    {
        $request->validate([
            'barcode' => 'required|string',
            'product_name' => 'required|string',
            'brand' => 'required|string'
        ]);

        $result = $this->halalService->verifyAndStore(
            $request->barcode,
            $request->product_name,
            $request->brand
        );

        return response()->json($result);
    }

    /**
     * Manual update halal status
     */
    public function manualUpdate(Request $request, $id)
    {
        $request->validate([
            'halal_status' => 'required|in:halal,non_halal,unknown',
            'halal_certificate_number' => 'nullable|string',
            'certification_body' => 'nullable|string',
            'certificate_valid_until' => 'nullable|date'
        ]);

        $product = HalalProduct::findOrFail($id);
        $product->update($request->all());

        return response()->json([
            'success' => true,
            'message' => 'Product updated successfully',
            'data' => $product
        ]);
    }

    /**
     * Batch verify products
     */
    public function batchVerify(Request $request)
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
     * Show product details
     */
    public function show($id)
    {
        $product = HalalProduct::with('product')->findOrFail($id);
        
        return view('admin.halal-products.show', compact('product'));
    }

    /**
     * Delete halal product record
     */
    public function destroy($id)
    {
        $product = HalalProduct::findOrFail($id);
        $product->delete();

        return response()->json([
            'success' => true,
            'message' => 'Product deleted successfully'
        ]);
    }

    /**
     * Re-check old products (background job trigger)
     */
    public function recheckOld()
    {
        $this->halalService->recheckOldProducts();

        return response()->json([
            'success' => true,
            'message' => 'Re-check process started'
        ]);
    }

    /**
     * Export halal products data
     */
    public function export()
    {
        $products = HalalProduct::with('product')->get();
        
        $csvData = [];
        $csvData[] = ['Barcode', 'Product Name', 'Brand', 'Halal Status', 'Certificate Number', 'Certification Body', 'Valid Until', 'Last Checked'];
        
        foreach ($products as $product) {
            $csvData[] = [
                $product->product_barcode,
                $product->product_name,
                $product->brand,
                $product->halal_status,
                $product->halal_certificate_number,
                $product->certification_body,
                $product->certificate_valid_until,
                $product->last_checked_at
            ];
        }

        $filename = 'halal_products_' . date('Y-m-d') . '.csv';
        
        $headers = [
            'Content-Type' => 'text/csv',
            'Content-Disposition' => "attachment; filename=\"$filename\"",
        ];

        return response()->stream(function() use ($csvData) {
            $file = fopen('php://output', 'w');
            foreach ($csvData as $row) {
                fputcsv($file, $row);
            }
            fclose($file);
        }, 200, $headers);
    }
}
