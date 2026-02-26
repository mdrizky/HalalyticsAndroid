<?php

namespace App\Console;

use Illuminate\Console\Scheduling\Schedule;
use Illuminate\Foundation\Console\Kernel as ConsoleKernel;
use App\Jobs\RecheckHalalProductsJob;

class Kernel extends ConsoleKernel
{
    protected function schedule(Schedule $schedule)
    {
        // Recheck halal products daily at 2 AM
        $schedule->job(new RecheckHalalProductsJob())
            ->dailyAt('02:00')
            ->withoutOverlapping();
            
        // Additional schedules can be added here
        // $schedule->command('telescope:prune')->daily();
    }

    protected function commands()
    {
        $this->load(__DIR__.'/Commands');

        require base_path('routes/console.php');
    }
}
