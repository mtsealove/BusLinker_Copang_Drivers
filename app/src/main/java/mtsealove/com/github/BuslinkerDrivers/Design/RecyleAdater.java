package mtsealove.com.github.BuslinkerDrivers.Design;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import mtsealove.com.github.BuslinkerDrivers.Entity.RunInfo;
import mtsealove.com.github.BuslinkerDrivers.R;

import java.util.List;

public class RecyleAdater extends RecyclerView.Adapter<RecyleAdater.ViewHolder> {
    Context context;
    List<RunInfo> runInfos;

    public RecyleAdater(Context context, List<RunInfo> runInfos) {
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
        viewHolder.cost.setText(runInfo.getCost() + "원");
    }

    @Override
    public int getItemCount() {
        return runInfos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView startAddr, startTime, endAddr, endTime, wayloadCnt, cost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            startAddr = itemView.findViewById(R.id.startTV);
            startTime = itemView.findViewById(R.id.startTimeTV);
            endAddr = itemView.findViewById(R.id.endTV);
            endTime = itemView.findViewById(R.id.endTimeTV);
            wayloadCnt = itemView.findViewById(R.id.wayloadTV);
            cost = itemView.findViewById(R.id.costTV);
        }
    }
}
