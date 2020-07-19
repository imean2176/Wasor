package com.wasor.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.widget.ANImageView;
import com.wasor.CachXuLyActivity;
import com.wasor.R;
import com.wasor.modal.Rac;

import java.util.List;

public class RacAdapter extends RecyclerView.Adapter{
    //Dữ liệu hiện thị là danh sách sinh viên
    private List mRacs;
    // Lưu Context để dễ dàng truy cập
    private Context mContext;

    View racView;

    public RacAdapter(List _rac, Context mContext) {
        this.mRacs = _rac;
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Nạp layout cho View biểu diễn phần tử sinh viên
        racView =
                inflater.inflate(R.layout.rac_listview_item, parent, false);

        RacHolder viewHolder = new RacHolder(racView);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final Rac rac = (Rac) mRacs.get(position);
        holder = (RacHolder)holder;
        ((RacHolder) holder).tenRac.setText(rac.getTenrac());
        ((RacHolder) holder).loaiRac.setText(rac.getLoairac());
        ((RacHolder) holder).image.setImageUrl(rac.getUrlImage());

        ((RacHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("rac", rac);

                //Hiển thị màn hình chi tiết loại rác
                Intent intent = new Intent(mContext, CachXuLyActivity.class);

                intent.putExtra("rac",bundle);

                //Truyền dữ liệu Rác qua màn hình chi tiết rác
                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mRacs.size();
    }

    /**
     * Lớp nắm giữ cấu trúc view
     */
    public class RacHolder extends RecyclerView.ViewHolder {
        private View itemview;
        public TextView tenRac;
        public TextView loaiRac;
        public ANImageView image;

        public RacHolder(View itemView) {
            super(itemView);
            itemview = itemView;
            tenRac = itemView.findViewById(R.id.listview_item_title);
            loaiRac = itemView.findViewById(R.id.listview_item_short_description);
            image = itemView.findViewById(R.id.listview_image);

//            //Xử lý khi nút Chi tiết được bấm
//            detail_button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Toast.makeText(view.getContext(),
//                            studentname.getText() +" | "
//                                    + " Demo function", Toast.LENGTH_SHORT)
//                            .show();
//                }
//            });
        }
    }
}
