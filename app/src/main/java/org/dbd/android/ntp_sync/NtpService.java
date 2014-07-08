package org.dbd.android.ntp_sync;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.Date;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class NtpService extends IntentService {
    private static final String ACTION_NTP_SYNC_TIME = "org.dbd.android.ntp_sync.action.FOO";

    private static final String EXTRA_PARAM1 = "org.dbd.android.ntp_sync.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "org.dbd.android.ntp_sync.extra.PARAM2";
    private static final String TAG = "NtpService";
    // Defines a custom Intent action
    public static final String BROADCAST_ACTION = "org.dbd.android.ntp_sync.NTP_SERVICE_RESULT_BROADCAST";
    public static final String EXTENDED_DATA_STATUS = "org.dbd.android.ntp_sync.STATUS"; // int type
    public static final String EXTENDED_DATA_TIME = "org.dbd.android.ntp_sync.TIME"; // long type
    public static final int STATUS_FINISH = 0x01;
    public static final int STATUS_START = 0x02;
    public static final int STATUS_FINISHED = 0x11;
    public static final int STATUS_STARTED = 0x12;
    public static final int STATUS_FAILED = 0x3;


    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startSyncTime(Context context) {
        Intent intent = new Intent(context, NtpService.class);
        intent.setAction(ACTION_NTP_SYNC_TIME);
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
                handleActionSyncTime();
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSyncTime() {
        Date syncDate;
        Log.e(TAG, "getUTCTime  :  " + (syncDate = getUTCTime()));

        // TODO set time for system (can't not set time if you don't a system apps).
        // TODO notify UI start analog clock
        // can't set system time. So, send time to UI for update UI.
        Intent localIntent = new Intent(BROADCAST_ACTION)
                // Puts the status into the Intent
                .putExtra(EXTENDED_DATA_STATUS, STATUS_FINISHED)
                        // Put sync time into the Intent to update UI
                .putExtra(EXTENDED_DATA_TIME, syncDate.getTime());
        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
        // TODO report with user service had finished

    }

//    public static Date getNTPDate() {
//        String[] hosts = new String[]{
//                "0.asia.pool.ntp.org", "1.asia.pool.ntp.org",
//                "2.asia.pool.ntp.org", "3.asia.pool.ntp.org"};
//        NTPUDPClient client = new NTPUDPClient();
//        // We want to timeout if a response takes longer than 5 seconds
//        client.setDefaultTimeout(5000);
//
//        for (String host : hosts) {
//
//            try {
//                InetAddress hostAddr = InetAddress.getByName(host);
//                System.out.println("> " + hostAddr.getHostName() + "/" + hostAddr.getHostAddress());
//                TimeInfo info = client.getTime(hostAddr);
//                Date date = new Date(info.getReturnTime());
//                return date;
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        client.close();
//
//        return null;
//
//    }

    public Date getUTCTime() {
        int mDefaultTimeOut = 5000;
        long nowAsPerDeviceTimeZone = 0;

        String[] hosts = new String[]{
                "0.asia.pool.ntp.org", "1.asia.pool.ntp.org",
                "2.asia.pool.ntp.org", "3.asia.pool.ntp.org"};

        SntpClient client = new SntpClient();

        for (String host : hosts) {
            try {
                InetAddress hostAddr = InetAddress.getByName(host);
                System.out.println("> " + hostAddr.getHostName() + "/" + hostAddr.getHostAddress());
                if (client.requestTime(hostAddr.getHostName(), mDefaultTimeOut)) {
                    nowAsPerDeviceTimeZone = client.getNtpTime();
                    return new Date(nowAsPerDeviceTimeZone);
                }

            } catch (IOException e) {
                // ignore
            }
        }
        return null;
    }
}
