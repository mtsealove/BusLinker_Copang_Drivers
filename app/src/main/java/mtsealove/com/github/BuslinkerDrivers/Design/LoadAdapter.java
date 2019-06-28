package mtsealove.com.github.BuslinkerDrivers.Design;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.drm.ProcessedData;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
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
        getCurrentLocation();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_loads, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
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
                }catch (ActivityNotFoundException e){   //카카오맵이 설치되어 있지 않은 경우
                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                    builder.setTitle("알림")
                            .setMessage("카카오맵이 설치되어 있지 않습니다\n플레이스토어로 연결하시겠습니까?")
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String url="https://play.google.com/store/apps/details?id=net.daum.android.map";
                            Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            context.startActivity(intent);
                        }
                    });
                    AlertDialog dialog=builder.create();
                    dialog.show();

                } catch (Exception e){
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
        try{
            Location currentLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            latitude=currentLocation.getLatitude();
            longitude=currentLocation.getLongitude();
        } catch (Exception e){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
            e.printStackTrace();
        }

    }

    LocationListener locationListener=new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            latitude=location.getLatitude();
            longitude=location.getLongitude();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
}
