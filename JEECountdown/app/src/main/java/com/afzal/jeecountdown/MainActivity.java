package com.afzal.jeecountdown;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import java.util.Calendar;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    // Target: JEE Main 2027 Session 1 — Jan 22, 2027, 9:00 AM IST
    private static final long TARGET_MS;
    private static final long START_MS;

    static {
        Calendar target = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"));
        target.set(2027, Calendar.JANUARY, 22, 9, 0, 0);
        target.set(Calendar.MILLISECOND, 0);
        TARGET_MS = target.getTimeInMillis();

        Calendar start = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"));
        start.set(2025, Calendar.JANUARY, 1, 0, 0, 0);
        start.set(Calendar.MILLISECOND, 0);
        START_MS = start.getTimeInMillis();
    }

    private static final String[] QUOTES = {
        "📐  Grind now · Celebrate later",
        "⚡  Every second counts",
        "🔢  Master concepts, not just formulas",
        "🚀  IIT Bombay awaits",
        "📖  One more problem before you sleep",
        "🎯  Focus beats talent every time",
        "⚛️  Physics · Chemistry · Mathematics",
        "🔥  Lock in. No excuses.",
        "💡  Consistency is the real cheat code",
        "🏆  Your rank is decided today, not on exam day"
    };

    private TextView tvDays, tvHours, tvMinutes, tvSeconds;
    private TextView tvWeeks, tvMonths, tvProgressPct, tvQuote, tvTitle;
    private ProgressBar progressBar;
    private CountDownTimer countDownTimer;
    private final Handler quoteHandler = new Handler(Looper.getMainLooper());
    private int quoteIndex = 0;
    private long prevDays = -1, prevHours = -1, prevMinutes = -1, prevSeconds = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        tvTitle       = findViewById(R.id.tv_title);
        tvDays        = findViewById(R.id.tv_days);
        tvHours       = findViewById(R.id.tv_hours);
        tvMinutes     = findViewById(R.id.tv_minutes);
        tvSeconds     = findViewById(R.id.tv_seconds);
        tvWeeks       = findViewById(R.id.tv_weeks);
        tvMonths      = findViewById(R.id.tv_months);
        tvProgressPct = findViewById(R.id.tv_progress_pct);
        progressBar   = findViewById(R.id.progress_bar);
        tvQuote       = findViewById(R.id.tv_quote);

        startCountdown();
        startQuoteCycle();
    }

    private void startCountdown() {
        long diff = TARGET_MS - System.currentTimeMillis();
        if (diff <= 0) { showExamDay(); return; }

        countDownTimer = new CountDownTimer(diff, 1000) {
            @Override public void onTick(long ms) { updateUI(ms); }
            @Override public void onFinish()       { showExamDay(); }
        }.start();
    }

    private void updateUI(long diff) {
        long totalSec = diff / 1000;
        long sec      = totalSec % 60;
        long min      = (totalSec / 60) % 60;
        long days     = totalSec / 86400;
        long weeks    = days / 7;

        // Hours remaining until midnight IST (New Delhi timezone)
        Calendar nowIST = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"));
        long hrs = 23 - nowIST.get(Calendar.HOUR_OF_DAY);

        // Calendar months remaining
        Calendar tCal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"));
        tCal.setTimeInMillis(TARGET_MS);
        Calendar nCal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"));
        long months = (tCal.get(Calendar.YEAR) - nCal.get(Calendar.YEAR)) * 12L
                    + (tCal.get(Calendar.MONTH) - nCal.get(Calendar.MONTH));

        if (days != prevDays)    { flipUpdate(tvDays,    pad(days, 3)); prevDays    = days;    }
        if (hrs  != prevHours)   { flipUpdate(tvHours,   pad(hrs,  2)); prevHours   = hrs;     }
        if (min  != prevMinutes) { flipUpdate(tvMinutes, pad(min,  2)); prevMinutes = min;     }
        if (sec  != prevSeconds) { flipUpdate(tvSeconds, pad(sec,  2)); prevSeconds = sec;     }

        tvWeeks.setText(pad(weeks, 2));
        tvMonths.setText(pad(months, 2));

        // Progress bar
        long totalDuration = TARGET_MS - START_MS;
        long elapsed       = System.currentTimeMillis() - START_MS;
        float pct = Math.min(100f, Math.max(0f, (elapsed * 100f) / totalDuration));
        progressBar.setProgress((int) pct);
        tvProgressPct.setText(String.format("%.1f%% elapsed", pct));
    }

    private void flipUpdate(final TextView tv, final String newVal) {
        tv.animate()
            .rotationX(-90f)
            .setDuration(140)
            .withEndAction(() -> {
                tv.setText(newVal);
                tv.setRotationX(90f);
                tv.animate()
                    .rotationX(0f)
                    .setDuration(140)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start();
            }).start();
    }

    private final Runnable quoteRunnable = new Runnable() {
        @Override public void run() {
            quoteIndex = (quoteIndex + 1) % QUOTES.length;
            tvQuote.animate().alpha(0f).setDuration(400).withEndAction(() -> {
                tvQuote.setText(QUOTES[quoteIndex]);
                tvQuote.animate().alpha(1f).setDuration(400).start();
            }).start();
            quoteHandler.postDelayed(this, 4000);
        }
    };

    private void startQuoteCycle() {
        tvQuote.setText(QUOTES[0]);
        quoteHandler.postDelayed(quoteRunnable, 4000);
    }

    private void showExamDay() {
        if (tvTitle != null) tvTitle.setText("EXAM DAY!");
    }

    private String pad(long n, int width) {
        return String.format("%0" + width + "d", n);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
        quoteHandler.removeCallbacks(quoteRunnable);
    }
}
