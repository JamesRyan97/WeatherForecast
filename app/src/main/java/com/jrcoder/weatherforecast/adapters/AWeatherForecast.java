package com.jrcoder.weatherforecast.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jrcoder.weatherforecast.R;
import com.jrcoder.weatherforecast.databinding.ItemWeatherForecastBinding;
import com.jrcoder.weatherforecast.models.ObjWeatherForecast;

import java.util.ArrayList;

public class AWeatherForecast extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final String TAG = AWeatherForecast.class.getSimpleName();

    private Context context;
    private ArrayList<ObjWeatherForecast> mListData;

    private final int VIEW_TYPE_ITEM = 1;
    private final int VIEW_TYPE_LOADING = 0;
    private boolean isLoaderVisible = false;

    private IListener listener;

    public void setOnClickListener(IListener listener) {
        this.listener = listener;
    }

    public interface IListener {
        void onClick(ObjWeatherForecast data, int position);
    }

    public AWeatherForecast(@NonNull Context context) {
        this.mListData = new ArrayList<>();
        this.context = context;
    }

    public AWeatherForecast(@NonNull Context context, @NonNull ArrayList<ObjWeatherForecast> mListData) {
        this.mListData = mListData;
        this.context = context;
    }

    public ArrayList<ObjWeatherForecast> getDataSource() {
        return mListData;
    }

    public void setDataSource(ArrayList<ObjWeatherForecast> mListData) {
        this.mListData = mListData;
        notifyDataSetChanged();
    }

    public void removeDataSource() {
        if(this.mListData != null)
            this.mListData.clear();
        notifyDataSetChanged();
    }

    /**
     * Add the item to the source
     *
     * @param item The item needs to be added
     */
    public void addItem(ObjWeatherForecast item) {
        mListData.add(item);
        notifyItemInserted(mListData.size());
    }

    /**
     * Add the list to the source
     *
     * @param listData The list needs to be added
     */
    public void addItems(ArrayList<ObjWeatherForecast> listData) {
        this.mListData.addAll(mListData.size(), listData);
        notifyItemRangeChanged(mListData.size() - listData.size(), mListData.size());
    }

    /**
     * Get item at position
     *
     * @param position The position you want to get the item
     * @return object
     */
    public ObjWeatherForecast getItem(int position) {
        return this.mListData.get(position);
    }

    /**
     * Remove the item from the list
     *
     * @param position position to be remove
     */
    public void removeItem(int position) {
        this.mListData.remove(position);
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return this.mListData.size() - 1 == position ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            return new ItemViewHolder(ItemWeatherForecastBinding.inflate(LayoutInflater.from(context), parent, false));
        } else {
            FrameLayout llLoadMore = new FrameLayout(context);
            ProgressBar progressBar = new ProgressBar(context);
            progressBar.setIndeterminate(true);
            progressBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#1DA1F2"), android.graphics.PorterDuff.Mode.MULTIPLY);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(40, 40);
            layoutParams.gravity = Gravity.CENTER;
            llLoadMore.addView(progressBar, layoutParams);
            return new LoadingViewHolder(llLoadMore);
        }
    }

    /**
     * Add load more view to the list (Object must have a no-argument constructor)
     */
    public void addViewLoadMore() {
        isLoaderVisible = true;
        mListData.add(new ObjWeatherForecast());
        notifyItemInserted(mListData.size() - 1);
    }

    /**
     * Remove load more view to the list
     */
    public void removeViewLoadMore() {
        isLoaderVisible = false;
        int position = mListData.size() - 1;
        ObjWeatherForecast item = getItem(position);
        if (item != null) {
            mListData.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            dataRows((ItemViewHolder) viewHolder, position);
        } else if (getItemViewType(position) == VIEW_TYPE_LOADING) {
            showLoadingView((LoadingViewHolder) viewHolder, position);
        }
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {

    }

    private void dataRows(ItemViewHolder holder, int position) {
        final ObjWeatherForecast item = mListData.get(position);

        item.setIconToImageView(context,holder.mView.imvWeather);
        holder.mView.tvTemperature.setText(String.format(context.getString(R.string.term_max_term_min),(int)item.getTermMax(),(int)item.getTermMin()));

        holder.mView.tvTime.setText(item.getDate());
        holder.mView.tvWeatherName.setText(item.getWeatherName());
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private ItemWeatherForecastBinding mView;

        public ItemViewHolder(ItemWeatherForecastBinding mView) {
            super(mView.getRoot());
            this.mView = mView;
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}