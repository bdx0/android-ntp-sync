package org.dbd.android.ntp_sync;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by duongbaoduy on 7/8/14.
 */
public class AnalogClockFragment extends BaseFragment {

    public AnalogClockFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_analog_clock, container, false);
    }
}
