<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up()
    {
        Schema::create('halal_products', function (Blueprint $table) {
            $table->id();
            $table->string('product_barcode')->unique();
            $table->string('product_name');
            $table->string('brand');
            $table->string('halal_certificate_number')->nullable();
            $table->enum('halal_status', ['halal', 'non_halal', 'unknown'])->default('unknown');
            $table->string('certification_body')->nullable(); // MUI, JAKIM, etc
            $table->date('certificate_valid_until')->nullable();
            $table->json('certificate_data')->nullable(); // data lengkap dari API
            $table->timestamp('last_checked_at')->nullable();
            $table->timestamps();
        });
    }

    public function down()
    {
        Schema::dropIfExists('halal_products');
    }
};
