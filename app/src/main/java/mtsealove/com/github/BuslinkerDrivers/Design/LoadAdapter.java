package mtsealove.com.github.BuslinkerDrivers.Design;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.drm.ProcessedData;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import mtsealove.com.github.BuslinkerDrivers.Entity.Load;
import mtsealove.com.github.BuslinkerDrivers.Entity.RunInfo;
import mtsealove.com.github.BuslinkerDrivers.R;
import mtsealove.com.github.BuslinkerDrivers.RunInfoActivity;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.List;

public class LoadAdapter extends RecyclerView.Adapter<LoadAdapter.ViewHolder> {
    Context context;
    List<Load> loads;

    public LoadAdapter(Context context, List<Load> loads) {
        this.context = context;
        this.loads = loads;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        getCurrentLocation();
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_loads, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        getCurrentLocation();
        final Load load = loads.get(i);
        viewHolder.Cat.setText(load.getCat());
        viewHolder.Address.setText(load.getAddress());
        viewHolder.Name.setText(load.getName());
        if (load.getTime() != null) {   //시간이 설정되어 있을 경우
            viewHolder.Time.setText(load.getTime());
        } else { //숨기기
            viewHolder.Time.setVisibility(View.GONE);
        }

        //뷰 클릭시
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //카카오 지도 열기
                try {
                    String url = null;
                    if (i != 0) {
                        url = "daummaps://route?"
                                + "sp=" + loads.get(i - 1).getLatitude() + "," + loads.get(i - 1).getLongitude()
                                + "&ep=" + loads.get(i).getLatitude() + "," + loads.get(i).getLongitude()
                                + "&by=CAR";
                    } else {
                        url = "daummaps://route?"
                                + "sp=" + latitude + "," + longitude
                                + "&ep=" + loads.get(0).getLatitude() + "," + loads.get(0).getLongitude()
                                + "&by=CAR";
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    context.startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return loads.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView Cat, Address, Time, Name;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            Name = itemView.findViewById(R.id.nameTV);
            Cat = itemView.findViewById(R.id.catTV);
            Address = itemView.findViewById(R.id.addressTV);
            Time = itemView.findViewById(R.id.timeTV);
        }
    }

    double latitude, longitude;

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location currentLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        latitude=currentLocation.getLatitude();
        longitude=currentLocation.getLongitude();
    }
}
