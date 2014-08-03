package io.lampshade.now.beacon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.Utils;

import java.util.ArrayList;
import java.util.Collection;

import io.lampshade.now.MainActivity;
import io.lampshade.now.R;


/**
 * Displays basic information about beacon.
 *
 * @author wiktor@estimote.com (Wiktor Gworek)
 */
public class LeDeviceListAdapter extends BaseAdapter {

  enum CurrentSuggestion{
    NONE, PURPLE, GREEN, BLUE
  }
  CurrentSuggestion suggestion = CurrentSuggestion.NONE;
  Context mContext;

  private ArrayList<Beacon> beacons;
  private LayoutInflater inflater;

  public LeDeviceListAdapter(Context context) {
    this.inflater = LayoutInflater.from(context);
    this.beacons = new ArrayList<Beacon>();
    mContext = context;
  }

  public void replaceWith(Collection<Beacon> newBeacons) {
    this.beacons.clear();
    this.beacons.addAll(newBeacons);
    notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return beacons.size();
  }

  @Override
  public Beacon getItem(int position) {
    return beacons.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View view, ViewGroup parent) {
    view = inflateIfRequired(view, position, parent);
    bind(getItem(position), view);
    return view;
  }

  private void bind(Beacon beacon, View view) {
    ViewHolder holder = (ViewHolder) view.getTag();
    holder.macTextView.setText(
        String.format("MAC: %s (%.2fm)", beacon.getMacAddress(), Utils.computeAccuracy(beacon)));
    holder.majorTextView.setText("Major: " + beacon.getMajor());
    holder.minorTextView.setText("Minor: " + beacon.getMinor());
    holder.measuredPowerTextView.setText("MPower: " + beacon.getMeasuredPower());
    holder.rssiTextView.setText("RSSI: " + beacon.getRssi());


    if(beacon.getMajor()==21946 && beacon.getMinor()==41785){
      // green

      if(this.suggestion!=CurrentSuggestion.GREEN){
        if(Utils.computeProximity(beacon).equals(Utils.Proximity.IMMEDIATE)){
          MainActivity.showNotification(mContext, "Office", "Party");
          this.suggestion = CurrentSuggestion.GREEN;
        }

      }

    } else if(beacon.getMajor()==59136 && beacon.getMinor()==39494){
      //purple


      if(this.suggestion!=CurrentSuggestion.PURPLE){
        if(Utils.computeProximity(beacon).equals(Utils.Proximity.IMMEDIATE)){
          MainActivity.showNotification(mContext, "Office", "OFF");
          this.suggestion = CurrentSuggestion.PURPLE;
        }

      }

    } else if (beacon.getMajor()==2408 && beacon.getMinor()==60956){
     // blue

      if(this.suggestion!=CurrentSuggestion.BLUE){
        if(Utils.computeProximity(beacon).equals(Utils.Proximity.IMMEDIATE)){
          MainActivity.showNotification(mContext, "Office", "Reading");
          this.suggestion =  CurrentSuggestion.BLUE;
        }

      }

    }

  }

  private View inflateIfRequired(View view, int position, ViewGroup parent) {
    if (view == null) {
      view = inflater.inflate(R.layout.device_item, null);
      view.setTag(new ViewHolder(view));
    }
    return view;
  }

  static class ViewHolder {
    final TextView macTextView;
    final TextView majorTextView;
    final TextView minorTextView;
    final TextView measuredPowerTextView;
    final TextView rssiTextView;

    ViewHolder(View view) {
      macTextView = (TextView) view.findViewWithTag("mac");
      majorTextView = (TextView) view.findViewWithTag("major");
      minorTextView = (TextView) view.findViewWithTag("minor");
      measuredPowerTextView = (TextView) view.findViewWithTag("mpower");
      rssiTextView = (TextView) view.findViewWithTag("rssi");
    }
  }
}
