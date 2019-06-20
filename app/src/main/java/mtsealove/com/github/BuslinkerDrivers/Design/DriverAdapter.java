package mtsealove.com.github.BuslinkerDrivers.Design;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import mtsealove.com.github.BuslinkerDrivers.Entity.Driver;
import mtsealove.com.github.BuslinkerDrivers.R;

import java.util.List;

public class DriverAdapter extends RecyclerView.Adapter<DriverAdapter.ViewHolder> {
    Context context;
    List<Driver> drivers;

    public DriverAdapter(Context context, List<Driver> drivers) {
        this.context = context;
        this.drivers=drivers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_drivers, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        //디스플레이
        final Driver driver=drivers.get(i);
        viewHolder.Name.setText(driver.getName());
        viewHolder.ID.setText(driver.getID());
        viewHolder.CarType.setText(driver.getCarType());
        viewHolder.CarYear.setText(Integer.toString(driver.getCarYear()));
        viewHolder.Contact.setText(driver.getContact());
    }

    @Override
    public int getItemCount() {
        return drivers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView Name, ID, CarType, CarYear, Contact;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView=itemView.findViewById(R.id.cardView);
            Name=itemView.findViewById(R.id.nameTV);
            ID=itemView.findViewById(R.id.IDTV);
            CarType=itemView.findViewById(R.id.carTypeTV);
            CarYear=itemView.findViewById(R.id.carYearTV);
            Contact=itemView.findViewById(R.id.contactTV);
        }
    }
}
