package com.beckytech.saayinsiiwaliigalaakutaa8ffaa.adapter;

import static com.beckytech.saayinsiiwaliigalaakutaa8ffaa.adapter.ImageSliderOneAdapter.*;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.R;
import com.beckytech.saayinsiiwaliigalaakutaa8ffaa.model.SliderModel;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

public class ImageSliderOneAdapter extends SliderViewAdapter<ImageSliderOneAdapter.MyHolder>{

    Context context;
    onImageClickListener imageClickListener;
    List<SliderModel> modelList;

    public interface onImageClickListener {
        void imageClick(SliderModel model);
    }

    public ImageSliderOneAdapter(Context context, List<SliderModel> modelList, onImageClickListener imageClickListener) {
        this.context = context;
        this.modelList = modelList;
        this.imageClickListener = imageClickListener;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_one, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder viewHolder, int position) {
        SliderModel model = modelList.get(position);
        viewHolder.relativeLayout.setBackgroundResource(model.getImage());
        viewHolder.itemView.setOnClickListener(v -> imageClickListener.imageClick(model));
    }

    @Override
    public int getCount() {
        return modelList.size();
    }

    static class MyHolder extends ViewHolder {
        private final RelativeLayout relativeLayout;
        public MyHolder(View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.relative);
        }
    }

}
