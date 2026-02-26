@extends('layouts.admin')

@section('title', 'Admin Dashboard')

@section('content')
<div class="container-fluid">
    <!-- Page Heading -->
    <div class="d-sm-flex align-items-center justify-content-between mb-4">
        <h1 class="h3 mb-0 text-gray-800">Admin Dashboard</h1>
        <div>
            <a href="{{ route('admin.halal-products.export') }}" class="btn btn-primary btn-sm">
                <i class="fas fa-download"></i> Export Data
            </a>
            <button onclick="refreshData()" class="btn btn-info btn-sm ml-2">
                <i class="fas fa-sync"></i> Refresh
            </button>
        </div>
    </div>

    <!-- Statistics Cards -->
    <div class="row">
        <!-- Total Products -->
        <div class="col-xl-3 col-md-6 mb-4">
            <div class="card border-left-primary shadow h-100 py-2">
                <div class="card-body">
                    <div class="row no-gutters align-items-center">
                        <div class="col mr-2">
                            <div class="text-xs font-weight-bold text-primary text-uppercase mb-1">
                                Total Products
                            </div>
                            <div class="h5 mb-0 font-weight-bold text-gray-800">{{ $stats['total_products'] }}</div>
                        </div>
                        <div class="col-auto">
                            <i class="fas fa-box fa-2x text-gray-300"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Halal Products -->
        <div class="col-xl-3 col-md-6 mb-4">
            <div class="card border-left-success shadow h-100 py-2">
                <div class="card-body">
                    <div class="row no-gutters align-items-center">
                        <div class="col mr-2">
                            <div class="text-xs font-weight-bold text-success text-uppercase mb-1">
                                Halal Products
                            </div>
                            <div class="h5 mb-0 font-weight-bold text-gray-800">{{ $stats['total_halal_products'] }}</div>
                        </div>
                        <div class="col-auto">
                            <i class="fas fa-check-circle fa-2x text-gray-300"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Non Halal Products -->
        <div class="col-xl-3 col-md-6 mb-4">
            <div class="card border-left-danger shadow h-100 py-2">
                <div class="card-body">
                    <div class="row no-gutters align-items-center">
                        <div class="col mr-2">
                            <div class="text-xs font-weight-bold text-danger text-uppercase mb-1">
                                Non Halal
                            </div>
                            <div class="h5 mb-0 font-weight-bold text-gray-800">{{ $stats['total_non_halal_products'] }}</div>
                        </div>
                        <div class="col-auto">
                            <i class="fas fa-times-circle fa-2x text-gray-300"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Unknown Products -->
        <div class="col-xl-3 col-md-6 mb-4">
            <div class="card border-left-warning shadow h-100 py-2">
                <div class="card-body">
                    <div class="row no-gutters align-items-center">
                        <div class="col mr-2">
                            <div class="text-xs font-weight-bold text-warning text-uppercase mb-1">
                                Unknown Status
                            </div>
                            <div class="h5 mb-0 font-weight-bold text-gray-800">{{ $stats['total_unknown_products'] }}</div>
                        </div>
                        <div class="col-auto">
                            <i class="fas fa-question-circle fa-2x text-gray-300"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- User Statistics -->
    <div class="row">
        <!-- Total Users -->
        <div class="col-xl-3 col-md-6 mb-4">
            <div class="card border-left-info shadow h-100 py-2">
                <div class="card-body">
                    <div class="row no-gutters align-items-center">
                        <div class="col mr-2">
                            <div class="text-xs font-weight-bold text-info text-uppercase mb-1">
                                Total Users
                            </div>
                            <div class="h5 mb-0 font-weight-bold text-gray-800">{{ $stats['total_users'] }}</div>
                        </div>
                        <div class="col-auto">
                            <i class="fas fa-users fa-2x text-gray-300"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Active Users Today -->
        <div class="col-xl-3 col-md-6 mb-4">
            <div class="card border-left-info shadow h-100 py-2">
                <div class="card-body">
                    <div class="row no-gutters align-items-center">
                        <div class="col mr-2">
                            <div class="text-xs font-weight-bold text-info text-uppercase mb-1">
                                Active Today
                            </div>
                            <div class="h5 mb-0 font-weight-bold text-gray-800">{{ $stats['active_users_today'] }}</div>
                        </div>
                        <div class="col-auto">
                            <i class="fas fa-user-check fa-2x text-gray-300"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Scans Today -->
        <div class="col-xl-3 col-md-6 mb-4">
            <div class="card border-left-primary shadow h-100 py-2">
                <div class="card-body">
                    <div class="row no-gutters align-items-center">
                        <div class="col mr-2">
                            <div class="text-xs font-weight-bold text-primary text-uppercase mb-1">
                                Scans Today
                            </div>
                            <div class="h5 mb-0 font-weight-bold text-gray-800">{{ $stats['scans_today'] }}</div>
                        </div>
                        <div class="col-auto">
                            <i class="fas fa-qrcode fa-2x text-gray-300"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Scans This Month -->
        <div class="col-xl-3 col-md-6 mb-4">
            <div class="card border-left-primary shadow h-100 py-2">
                <div class="card-body">
                    <div class="row no-gutters align-items-center">
                        <div class="col mr-2">
                            <div class="text-xs font-weight-bold text-primary text-uppercase mb-1">
                                Scans This Month
                            </div>
                            <div class="h5 mb-0 font-weight-bold text-gray-800">{{ $stats['scans_this_month'] }}</div>
                        </div>
                        <div class="col-auto">
                            <i class="fas fa-calendar fa-2x text-gray-300"></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Charts -->
    <div class="row">
        <!-- Scan Chart -->
        <div class="col-xl-8 col-lg-7">
            <div class="card shadow mb-4">
                <div class="card-header py-3 d-flex flex-row align-items-center justify-content-between">
                    <h6 class="m-0 font-weight-bold text-primary">Scan Activity (Last 7 Days)</h6>
                </div>
                <div class="card-body">
                    <div class="chart-area">
                        <canvas id="scanChart"></canvas>
                    </div>
                </div>
            </div>
        </div>

        <!-- Halal Status Chart -->
        <div class="col-xl-4 col-lg-5">
            <div class="card shadow mb-4">
                <div class="card-header py-3">
                    <h6 class="m-0 font-weight-bold text-primary">Halal Status Distribution</h6>
                </div>
                <div class="card-body">
                    <div class="chart-pie pt-4 pb-2">
                        <canvas id="halalChart"></canvas>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Recent Activities -->
    <div class="row">
        <!-- Recent Scans -->
        <div class="col-lg-6 mb-4">
            <div class="card shadow">
                <div class="card-header py-3">
                    <h6 class="m-0 font-weight-bold text-primary">Recent Scans</h6>
                </div>
                <div class="card-body">
                    @if($recentScans->count() > 0)
                        <div class="table-responsive">
                            <table class="table table-sm">
                                <thead>
                                    <tr>
                                        <th>User</th>
                                        <th>Product</th>
                                        <th>Status</th>
                                        <th>Time</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    @foreach($recentScans as $scan)
                                        <tr>
                                            <td>{{ $scan->user_name ?? 'Unknown' }}</td>
                                            <td>{{ $scan->barcode }}</td>
                                            <td>
                                                @if($scan->status_halal == 'halal')
                                                    <span class="badge badge-success">Halal</span>
                                                @elseif($scan->status_halal == 'haram')
                                                    <span class="badge badge-danger">Haram</span>
                                                @else
                                                    <span class="badge badge-warning">Unknown</span>
                                                @endif
                                            </td>
                                            <td>{{ $scan->created_at->diffForHumans() }}</td>
                                        </tr>
                                    @endforeach
                                </tbody>
                            </table>
                        </div>
                    @else
                        <p class="text-muted">No recent scans found.</p>
                    @endif
                </div>
            </div>
        </div>

        <!-- Recent Halal Checks -->
        <div class="col-lg-6 mb-4">
            <div class="card shadow">
                <div class="card-header py-3">
                    <h6 class="m-0 font-weight-bold text-primary">Recent Halal Checks</h6>
                </div>
                <div class="card-body">
                    @if($recentHalalChecks->count() > 0)
                        <div class="table-responsive">
                            <table class="table table-sm">
                                <thead>
                                    <tr>
                                        <th>Product</th>
                                        <th>Status</th>
                                        <th>Source</th>
                                        <th>Checked</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    @foreach($recentHalalChecks as $check)
                                        <tr>
                                            <td>{{ $check->product_name }}</td>
                                            <td>
                                                @if($check->halal_status == 'halal')
                                                    <span class="badge badge-success">Halal</span>
                                                @elseif($check->halal_status == 'non_halal')
                                                    <span class="badge badge-danger">Non Halal</span>
                                                @else
                                                    <span class="badge badge-warning">Unknown</span>
                                                @endif
                                            </td>
                                            <td>{{ $check->certification_body ?? 'Manual' }}</td>
                                            <td>{{ $check->last_checked_at->diffForHumans() }}</td>
                                        </tr>
                                    @endforeach
                                </tbody>
                            </table>
                        </div>
                    @else
                        <p class="text-muted">No recent halal checks found.</p>
                    @endif
                </div>
            </div>
        </div>
    </div>

    <!-- Top Scanned Products -->
    <div class="row">
        <div class="col-12 mb-4">
            <div class="card shadow">
                <div class="card-header py-3">
                    <h6 class="m-0 font-weight-bold text-primary">Top Scanned Products</h6>
                </div>
                <div class="card-body">
                    @if($topScanned->count() > 0)
                        <div class="table-responsive">
                            <table class="table table-sm">
                                <thead>
                                    <tr>
                                        <th>Rank</th>
                                        <th>Barcode</th>
                                        <th>Scan Count</th>
                                        <th>Percentage</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    @foreach($topScanned as $index => $product)
                                        <tr>
                                            <td>{{ $index + 1 }}</td>
                                            <td>{{ $product->product_barcode }}</td>
                                            <td>{{ $product->scan_count }}</td>
                                            <td>
                                                <div class="progress">
                                                    <div class="progress-bar" role="progressbar" 
                                                         style="width: {{ ($product->scan_count / $topScanned->first()->scan_count) * 100 }}%">
                                                        {{ round(($product->scan_count / $topScanned->first()->scan_count) * 100, 1) }}%
                                                    </div>
                                                </div>
                                            </td>
                                        </tr>
                                    @endforeach
                                </tbody>
                            </table>
                        </div>
                    @else
                        <p class="text-muted">No scan data available.</p>
                    @endif
                </div>
            </div>
        </div>
    </div>
</div>

@endsection

@push('scripts')
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script>
// Scan Chart
const scanCtx = document.getElementById('scanChart').getContext('2d');
const scanData = @json($scanChart);
new Chart(scanCtx, {
    type: 'line',
    data: {
        labels: scanData.map(item => item.date),
        datasets: [{
            label: 'Scans',
            data: scanData.map(item => item.count),
            borderColor: 'rgb(75, 192, 192)',
            backgroundColor: 'rgba(75, 192, 192, 0.2)',
            tension: 0.1
        }]
    },
    options: {
        responsive: true,
        scales: {
            y: {
                beginAtZero: true
            }
        }
    }
});

// Halal Status Chart
const halalCtx = document.getElementById('halalChart').getContext('2d');
const halalData = @json($halalChart);
new Chart(halalCtx, {
    type: 'doughnut',
    data: {
        labels: halalData.map(item => item.status),
        datasets: [{
            data: halalData.map(item => item.count),
            backgroundColor: halalData.map(item => item.color),
        }]
    },
    options: {
        responsive: true,
        plugins: {
            legend: {
                position: 'bottom'
            }
        }
    }
});

// Refresh data function
function refreshData() {
    location.reload();
}

// Auto-refresh every 5 minutes
setInterval(refreshData, 300000);
</script>
@endpush
