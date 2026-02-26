<?php

return [
    // MUI API Configuration
    'mui' => [
        'api_url' => env('MUI_API_URL', 'https://api.halalmui.org'),
        'api_key' => env('MUI_API_KEY'),
        'timeout' => env('MUI_API_TIMEOUT', 30),
    ],
    
    // JAKIM (Malaysia) API Configuration
    'jakim' => [
        'api_url' => env('JAKIM_API_URL', 'https://www.islam.gov.my'),
        'api_key' => env('JAKIM_API_KEY'),
        'timeout' => env('JAKIM_API_TIMEOUT', 30),
    ],
    
    // Halal Korea API Configuration
    'korea' => [
        'api_url' => env('KOREA_HALAL_API_URL'),
        'api_key' => env('KOREA_HALAL_API_KEY'),
        'timeout' => env('KOREA_HALAL_API_TIMEOUT', 30),
    ],
    
    // Scraping Configuration
    'scraping' => [
        'enabled' => env('HALAL_SCRAPING_ENABLED', true),
        'user_agent' => env('SCRAPING_USER_AGENT', 'HalalyticsBot/1.0'),
        'delay_between_requests' => env('SCRAPING_DELAY', 2), // seconds
        'max_retries' => env('SCRAPING_MAX_RETRIES', 3),
    ],
    
    // Cache Configuration
    'cache' => [
        'product_check_duration' => env('HALAL_CACHE_DURATION', 259200), // 30 days in seconds
        'api_response_duration' => env('HALAL_API_CACHE_DURATION', 300), // 5 minutes
        'stats_duration' => env('HALAL_STATS_CACHE_DURATION', 3600), // 1 hour
    ],
    
    // Background Job Configuration
    'background_jobs' => [
        'recheck_enabled' => env('HALAL_RECHECK_ENABLED', true),
        'recheck_interval' => env('HALAL_RECHECK_INTERVAL', '0 2 * * *'), // Daily at 2 AM
        'batch_size' => env('HALAL_BATCH_SIZE', 100),
    ],
];
