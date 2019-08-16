package mtsealove.com.github.BuslinkerDrivers.Design;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import mtsealove.com.github.BuslinkerDrivers.R;
import mtsealove.com.github.BuslinkerDrivers.RunInfoActivity;
import mtsealove.com.github.BuslinkerDrivers.Restful.RunInfo;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RunInfoAdapter extends RecyclerView.Adapter<RunInfoAdapter.ViewHolder> {
    Context context;
    List<RunInfo> runInfos;
    String CompanyID;
    String userID;
    int cat;

    public RunInfoAdapter(Context context, List<RunInfo> runInfos) {
        this.context = context;
        this.runInfos = runInfos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_run_info, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        // 작성 요망
        final RunInfo runInfo = runInfos.get(i);
        viewHolder.startAddr.setText(runInfo.getStartAddr());
        viewHolder.startTime.setText(runInfo.getStartTime());
        viewHolder.endAddr.setText(runInfo.getEndAddr());
        viewHolder.endTime.setText(runInfo.getEndTime());
        viewHolder.wayloadCnt.setText(runInfo.getWayloadCnt() + "개 ");
        viewHolder.cost.setText(runInfo.getCharge() + "원");

        Date date=new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat=new SimpleDateFormat("yy-MM-dd");
        if(runInfo.getRunDate().equals(dateFormat.format(date))) {
            viewHolder.RunDateTV.setTextColor(Color.parseColor("#FFFFFF"));
            viewHolder.RunDateTV.setText(runInfo.getRunDate()+" 오늘");
        } else
            viewHolder.RunDateTV.setText(runInfo.getRunDate());


        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, RunInfoActivity.class);
                intent.putExtra("RunInfo", runInfo);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return runInfos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView startAddr, startTime, endAddr, endTime, wayloadCnt, cost, RunDateTV;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            RunDateTV=itemView.findViewById(R.id.RunDateTV);
            cardView=itemView.findViewById(R.id.cardView);
            startAddr = itemView.findViewById(R.id.startTV);
            startTime = itemView.findViewById(R.id.startTimeTV);
            endAddr = itemView.findViewById(R.id.IDTV);
            endTime = itemView.findViewById(R.id.endTimeTV);
            wayloadCnt = itemView.findViewById(R.id.wayloadTV);
            cost = itemView.findViewById(R.id.costTV);
        }
    }

    public void setCompanyID(String companyID) {
        CompanyID = companyID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getCat() {
        return cat;
    }

    public void setCat(int cat) {
        this.cat = cat;
    }
}
