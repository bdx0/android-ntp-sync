package org.dbd.android.ntp_sync;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;


public class MyActivity extends ActionBarActivity implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private ViewPager mViewPager;
    /**
     * The {@link android.support.v4.view.ViewPager} that will host the section contents.
     */
    private TabHost mTabHost;
    private PagerAdapter mPagerAdapter;
    private HashMap<String, TabInfo> mapTabInfo = new HashMap<String, TabInfo>();

    // ==================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        // Initialise the TabHost
        this.initialiseTabHost(savedInstanceState);
        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab")); //set the tab as per the saved state
        }
        intialiseViewPager();
        // initialize with service
        initialiseService();
    }

    // ==================================================================
    // private for UI

    private void initialiseService() {
        IntentFilter statusIntent = new IntentFilter(NtpService.BROADCAST_ACTION);
        // Instantiates a new DownloadStateReceiver
        NtpResultReceiver resultReceiver = new NtpResultReceiver();
        // Registers the DownloadStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(resultReceiver, statusIntent);
        startSync(); // start ntp service
    }

    /**
     * Initialise ViewPager
     */
    private void intialiseViewPager() {
        List<Fragment> fragments = new Vector<Fragment>();
        fragments.add(Fragment.instantiate(this, AnalogClockFragment.class.getName()));
        fragments.add(Fragment.instantiate(this, DigitalClockFragment.class.getName()));
        this.mPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), fragments);
        this.mViewPager = (ViewPager) super.findViewById(R.id.pager_tab_content);
        this.mViewPager.setAdapter(this.mPagerAdapter);
        this.mViewPager.setOnPageChangeListener(this);
    }

    /**
     * Initialise the Tab Host
     */
    private void initialiseTabHost(Bundle args) {
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();
        TabInfo tabInfo = null;
        AddTab(this, this.mTabHost, this.mTabHost.newTabSpec("1").setIndicator("Analog Clock"), (tabInfo = new TabInfo("1", AnalogClockFragment.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        AddTab(this, this.mTabHost, this.mTabHost.newTabSpec("2").setIndicator("Digital Clock"), (tabInfo = new TabInfo("2", DigitalClockFragment.class, args)));
        this.mapTabInfo.put(tabInfo.tag, tabInfo);
        mTabHost.setOnTabChangedListener(this);
    }

    private static void AddTab(MyActivity activity, TabHost tabHost, TabHost.TabSpec tabSpec, TabInfo tabInfo) {
        // Attach a Tab view factory to the spec
        tabSpec.setContent(activity.new TabFactory(activity));
        tabHost.addTab(tabSpec);
    }

    // ==================================================================

    /**
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os.Bundle)
     */
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("tab", mTabHost.getCurrentTabTag()); //save the tab selected
        super.onSaveInstanceState(outState);
    }

    // ==================================================================
    @Override
    public void onTabChanged(String tabId) {
        //TabInfo newTab = this.mapTabInfo.get(tag);
        int pos = this.mTabHost.getCurrentTab();
        this.mViewPager.setCurrentItem(pos);
    }

    // ==================================================================
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        this.mTabHost.setCurrentTab(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    // ==================================================================
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_ntp_sync) {
            Toast.makeText(this, "ntp sync service call", Toast.LENGTH_SHORT).show();
            startSync();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // ==================================================================
    private class TabInfo {
        private String tag;
        private Class<?> clazz;
        private Bundle args;
        private Fragment fragment;

        TabInfo(String tag, Class<?> clazz, Bundle args) {
            this.tag = tag;
            this.clazz = clazz;
            this.args = args;
        }

    }


    // ==================================================================
    // private function

    private void startSync() {
        NtpService.startSyncTime(this);
    }

    // ==================================================================

    /**
     * A simple factory that returns dummy views to the Tabhost
     *
     * @author mwho
     */
    class TabFactory implements TabHost.TabContentFactory {

        private final Context mContext;

        /**
         * @param context
         */
        public TabFactory(Context context) {
            mContext = context;
        }

        /**
         * (non-Javadoc)
         *
         * @see android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String)
         */
        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }

    }
    // ==================================================================

    // Broadcast receiver for receiving status updates from the IntentService
    private class NtpResultReceiver extends BroadcastReceiver {

        private static final String TAG = "NtpResultReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "sync had finished & system time set");
            // TODO start analog clock with timer & customize UI.
            // TODO set alarm sync at the next time (10 minutes)
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Calendar alarmTime = Calendar.getInstance();
            alarmTime.set(Calendar.MINUTE, alarmTime.get(Calendar.MINUTE) + 10);
            alarmTime.set(Calendar.SECOND, 0);
            alarmTime.set(Calendar.MILLISECOND, 0);
            Intent i = new Intent(context, NtpService.class);

            PendingIntent pi = PendingIntent.getService(context, 1, i, PendingIntent.FLAG_NO_CREATE);
            if (pi == null) {
                pi = PendingIntent.getService(context, 1, i, PendingIntent.FLAG_ONE_SHOT);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5 // next feature in 5ms
                        , 60000 // 1000 * 60 * 10 + 5  ----  Millisec * Second * Minute at next feature 5ms
                        , pi);
            }
            // TODO restart timer for count down to sync.

        }
    }
}
