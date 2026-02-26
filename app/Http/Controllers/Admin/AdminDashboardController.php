<?php

namespace App\Http\Controllers\Admin;

use App\Http\Controllers\Controller;
use App\Models\HalalProduct;
use App\Models\Product;
use App\Models\User;
use App\Services\HalalCertificationService;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Carbon\Carbon;

class AdminDashboardController extends Controller
{
    protected $halalService;

    public function __construct(HalalCertificationService $halalService)
    {
        $this->halalService = $halalService;
    }

    /**
     * Display admin dashboard
     */
    public function index()
    {
        // Get statistics
        $stats = [
            'total_products' => Product::count(),
            'total_halal_products' => HalalProduct::halal()->count(),
            'total_non_halal_products' => HalalProduct::nonHalal()->count(),
            'total_unknown_products' => HalalProduct::unknown()->count(),
            'total_users' => User::count(),
            'active_users_today' => User::whereDate('last_login_at', today())->count(),
            'scans_today' => DB::table('scan_history')->whereDate('created_at', today())->count(),
            'scans_this_month' => DB::table('scan_history')->whereMonth('created_at', now()->month)->count(),
        ];

        // Get recent activities
        $recentScans = DB::table('scan_history')
            ->with('user')
            ->latest()
            ->limit(10)
            ->get();

        $recentHalalChecks = HalalProduct::with('product')
            ->latest('last_checked_at')
            ->limit(10)
            ->get();

        // Get chart data for last 7 days
        $scanChart = $this->getScanChartData();
        $halalChart = $this->getHalalChartData();

        // Get top scanned products
        $topScanned = DB::table('scan_history')
            ->select('product_barcode', DB::raw('count(*) as scan_count'))
            ->groupBy('product_barcode')
            ->orderBy('scan_count', 'desc')
            ->limit(10)
            ->get();

        return view('admin.dashboard.index', compact(
            'stats', 
            'recentScans', 
            'recentHalalChecks', 
            'scanChart', 
            'halalChart', 
            'topScanned'
        ));
    }

    /**
     * Get scan chart data for last 7 days
     */
    private function getScanChartData()
    {
        $data = [];
        for ($i = 6; $i >= 0; $i--) {
            $date = Carbon::now()->subDays($i);
            $count = DB::table('scan_history')
                ->whereDate('created_at', $date)
                ->count();
            
            $data[] = [
                'date' => $date->format('M d'),
                'count' => $count
            ];
        }
        return $data;
    }

    /**
     * Get halal status chart data
     */
    private function getHalalChartData()
    {
        $halal = HalalProduct::halal()->count();
        $nonHalal = HalalProduct::nonHalal()->count();
        $unknown = HalalProduct::unknown()->count();

        return [
            ['status' => 'Halal', 'count' => $halal, 'color' => '#22C55E'],
            ['status' => 'Non Halal', 'count' => $nonHalal, 'color' => '#EF4444'],
            ['status' => 'Unknown', 'count' => $unknown, 'color' => '#F59E0B']
        ];
    }

    /**
     * Get products that need rechecking
     */
    public function getProductsNeedingRecheck()
    {
        $products = HalalProduct::where('last_checked_at', '<', now()->subDays(30))
            ->orWhere('halal_status', 'unknown')
            ->with('product')
            ->limit(50)
            ->get();

        return response()->json([
            'success' => true,
            'data' => $products
        ]);
    }

    /**
     * Recheck products
     */
    public function recheckProducts(Request $request)
    {
        $productIds = $request->input('product_ids', []);
        $results = [];

        foreach ($productIds as $productId) {
            $halalProduct = HalalProduct::find($productId);
            if ($halalProduct) {
                $result = $this->halalService->verifyAndStore(
                    $halalProduct->product_barcode,
                    $halalProduct->product_name,
                    $halalProduct->brand
                );
                $results[] = [
                    'product_id' => $productId,
                    'success' => $result['success'],
                    'status' => $result['data']->halal_status,
                    'source' => $result['source']
                ];
            }
        }

        return response()->json([
            'success' => true,
            'data' => $results
        ]);
    }

    /**
     * Export dashboard data
     */
    public function exportData(Request $request)
    {
        $type = $request->input('type', 'all');
        
        switch ($type) {
            case 'scans':
                return $this->exportScanData();
            case 'halal':
                return $this->exportHalalData();
            case 'users':
                return $this->exportUserData();
            default:
                return $this->exportAllData();
        }
    }

    private function exportScanData()
    {
        $scans = DB::table('scan_history')
            ->join('users', 'scan_history.user_id', '=', 'users.id')
            ->select(
                'scan_history.*',
                'users.name as user_name',
                'users.email as user_email'
            )
            ->latest()
            ->get();

        $csvData = [];
        $csvData[] = ['Date', 'User', 'Email', 'Barcode', 'Product Name', 'Status'];

        foreach ($scans as $scan) {
            $csvData[] = [
                $scan->created_at,
                $scan->user_name,
                $scan->user_email,
                $scan->barcode,
                $scan->product_name ?? 'Unknown',
                $scan->status_halal ?? 'Unknown'
            ];
        }

        return $this->downloadCSV($csvData, 'scan_history_' . date('Y-m-d') . '.csv');
    }

    private function exportHalalData()
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

        return $this->downloadCSV($csvData, 'halal_products_' . date('Y-m-d') . '.csv');
    }

    private function exportUserData()
    {
        $users = User::latest()->get();

        $csvData = [];
        $csvData[] = ['Name', 'Email', 'Country', 'Join Date', 'Last Login', 'Total Scans'];

        foreach ($users as $user) {
            $scanCount = DB::table('scan_history')->where('user_id', $user->id)->count();
            $csvData[] = [
                $user->name,
                $user->email,
                $user->country,
                $user->created_at,
                $user->last_login_at,
                $scanCount
            ];
        }

        return $this->downloadCSV($csvData, 'users_' . date('Y-m-d') . '.csv');
    }

    private function downloadCSV($data, $filename)
    {
        $headers = [
            'Content-Type' => 'text/csv',
            'Content-Disposition' => "attachment; filename=\"$filename\"",
        ];

        $callback = function() use ($data) {
            $file = fopen('php://output', 'w');
            foreach ($data as $row) {
                fputcsv($file, $row);
            }
            fclose($file);
        };

        return response()->stream($callback, 200, $headers);
    }

    /**
     * Get system health status
     */
    public function systemHealth()
    {
        $health = [
            'database' => $this->checkDatabaseHealth(),
            'api_mui' => $this->checkMUIApiHealth(),
            'cache' => $this->checkCacheHealth(),
            'queue' => $this->checkQueueHealth(),
            'storage' => $this->checkStorageHealth(),
        ];

        return response()->json([
            'success' => true,
            'data' => $health
        ]);
    }

    private function checkDatabaseHealth()
    {
        try {
            DB::select('SELECT 1');
            return ['status' => 'healthy', 'message' => 'Database connection OK'];
        } catch (\Exception $e) {
            return ['status' => 'error', 'message' => $e->getMessage()];
        }
    }

    private function checkMUIApiHealth()
    {
        try {
            $response = \Http::timeout(10)->get(config('services.mui.api_url') . '/health');
            return $response->successful() 
                ? ['status' => 'healthy', 'message' => 'MUI API OK']
                : ['status' => 'error', 'message' => 'MUI API not responding'];
        } catch (\Exception $e) {
            return ['status' => 'error', 'message' => $e->getMessage()];
        }
    }

    private function checkCacheHealth()
    {
        try {
            \Cache::put('health_check', 'ok', 60);
            $value = \Cache::get('health_check');
            return $value === 'ok' 
                ? ['status' => 'healthy', 'message' => 'Cache OK']
                : ['status' => 'error', 'message' => 'Cache not working'];
        } catch (\Exception $e) {
            return ['status' => 'error', 'message' => $e->getMessage()];
        }
    }

    private function checkQueueHealth()
    {
        try {
            $failedJobs = DB::table('failed_jobs')->count();
            return [
                'status' => $failedJobs < 10 ? 'healthy' : 'warning',
                'message' => "Failed jobs: $failedJobs"
            ];
        } catch (\Exception $e) {
            return ['status' => 'error', 'message' => $e->getMessage()];
        }
    }

    private function checkStorageHealth()
    {
        try {
            $free = disk_free_space(storage_path());
            $total = disk_total_space(storage_path());
            $used = $total - $free;
            $percent = ($used / $total) * 100;

            return [
                'status' => $percent < 90 ? 'healthy' : 'warning',
                'message' => "Storage used: " . round($percent, 2) . "%"
            ];
        } catch (\Exception $e) {
            return ['status' => 'error', 'message' => $e->getMessage()];
        }
    }
}
