package org.dbd.android.ntp_sync;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.apache.http.impl.cookie.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class NtpService extends IntentService {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_NTP_SYNC_TIME = "org.dbd.android.ntp_sync.action.FOO";

    private static final String EXTRA_PARAM1 = "org.dbd.android.ntp_sync.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "org.dbd.android.ntp_sync.extra.PARAM2";
    private static final String TAG = "NtpService";

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startSyncTime(Context context, String param1, String param2) {
        Intent intent = new Intent(context, NtpService.class);
        intent.setAction(ACTION_NTP_SYNC_TIME);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public NtpService() {
        super("NtpService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_NTP_SYNC_TIME.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionSyncTime(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSyncTime(String param1, String param2) {
        Log.e(TAG, "getUTCTime  :  " + getUTCTime());
    }

    public String getUTCTime() {
        long nowAsPerDeviceTimeZone = 0;
        SntpClient sntpClient = new SntpClient();

        if (sntpClient.requestTime("0.africa.pool.ntp.org", 30000)) {
            nowAsPerDeviceTimeZone = sntpClient.getNtpTime();
            Calendar cal = Calendar.getInstance();
            TimeZone timeZoneInDevice = cal.getTimeZone();
            int differentialOfTimeZones = timeZoneInDevice.getOffset(System.currentTimeMillis());
            nowAsPerDeviceTimeZone -= differentialOfTimeZones;
        }
        return DateUtils.formatDate(new Date(nowAsPerDeviceTimeZone));
    }
}
