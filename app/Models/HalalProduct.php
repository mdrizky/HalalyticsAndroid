<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class HalalProduct extends Model
{
    protected $fillable = [
        'product_barcode',
        'product_name',
        'brand',
        'halal_certificate_number',
        'halal_status',
        'certification_body',
        'certificate_valid_until',
        'certificate_data',
        'last_checked_at'
    ];

    protected $casts = [
        'certificate_data' => 'array',
        'certificate_valid_until' => 'date',
        'last_checked_at' => 'datetime'
    ];

    // Relasi dengan produk utama
    public function product()
    {
        return $this->belongsTo(Product::class, 'product_barcode', 'barcode');
    }

    // Scope untuk status halal
    public function scopeHalal($query)
    {
        return $query->where('halal_status', 'halal');
    }

    public function scopeNonHalal($query)
    {
        return $query->where('halal_status', 'non_halal');
    }

    public function scopeUnknown($query)
    {
        return $query->where('halal_status', 'unknown');
    }

    // Check if certificate is still valid
    public function isCertificateValid()
    {
        return $this->certificate_valid_until && 
               $this->certificate_valid_until->isFuture();
    }

    // Get status badge color
    public function getStatusColor()
    {
        return match($this->halal_status) {
            'halal' => 'success',
            'non_halal' => 'danger',
            'unknown' => 'warning',
            default => 'secondary'
        };
    }
}
