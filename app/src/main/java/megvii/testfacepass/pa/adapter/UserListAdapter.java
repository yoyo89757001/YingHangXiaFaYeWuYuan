package megvii.testfacepass.pa.adapter;

import android.content.Context;


import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


import java.util.List;


import io.objectbox.query.LazyList;
import mcv.facepass.FacePassHandler;

import megvii.testfacepass.pa.R;
import megvii.testfacepass.pa.beans.Subject;

import megvii.testfacepass.pa.view.GlideRoundTransform;


/**
 * Created by xingchaolei on 2017/12/5.
 */

public class UserListAdapter extends BaseAdapter {

    private List<Subject> mGroupNames;
    private LayoutInflater mLayoutInflater;
    private ItemDeleteButtonClickListener mItemDeleteButtonClickListener;
    private FacePassHandler facePassHandler=null;
    private Context context;
    private RequestOptions myOptions2 =null;


    public UserListAdapter(List<Subject> data, Context context,FacePassHandler facePassHandler) {
        mGroupNames=data;
        this.context=context;
        myOptions2 = new RequestOptions()
                .fitCenter()
                .error(R.drawable.erroy_bg)
                //   .transform(new GlideCircleTransform(MyApplication.myApplication, 2, Color.parseColor("#ffffffff")));
                .transform(new GlideRoundTransform(context, 20));
        this.facePassHandler=facePassHandler;
    }

    public List<Subject> getData() {
        return mGroupNames;
    }

    public void setData(LazyList<Subject> data) {
        mGroupNames = data;
    }

    public void setOnItemDeleteButtonClickListener(ItemDeleteButtonClickListener listener) {
        mItemDeleteButtonClickListener = listener;
    }

    @Override
    public int getCount() {
        if (mGroupNames==null || mGroupNames.size()==0 || null==mGroupNames.get(0)){
           // Log.d("UserListAdapter", "mGroupNames.size():000");
            return 0;
        }else {
          //  Log.d("UserListAdapter", "mGroupNames.size():" + mGroupNames.size());
            return mGroupNames.size();
        }

    }

    @Override
    public Object getItem(int position) {
        if (mGroupNames==null || mGroupNames.size()==0){
            return null;
        }else {
            return  mGroupNames.get(position);
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (mLayoutInflater == null) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }
        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.layout_item_group_nameuser, parent, false);
            holder = new ViewHolder();
            holder.groupNameTv =  convertView.findViewById(R.id.tv_group_name);
            holder.deleteGroupIv =  convertView.findViewById(R.id.iv_delete_group);
            holder.touxiang =  convertView.findViewById(R.id.touxiang);
            holder.kahao=convertView.findViewById(R.id.kahao);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.deleteGroupIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemDeleteButtonClickListener != null) {
                    mItemDeleteButtonClickListener.OnItemDeleteButtonClickListener(position);
                }
            }
        });

        holder.groupNameTv.setText(mGroupNames.get(position).getName());

        holder.kahao.setText("ID: "+mGroupNames.get(position).getSid()+" 过期时间:"+mGroupNames.get(position).getBirthday());

        try {
            if (mGroupNames.get(position).getTeZhengMa()!=null && facePassHandler!=null){
                Glide.with(context)
                        .load(new BitmapDrawable(context.getResources(),facePassHandler.getFaceImage(mGroupNames.get(position).getTeZhengMa().getBytes())))
                        .apply(myOptions2)
                        .into(holder.touxiang);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }


    public static class ViewHolder {
        TextView groupNameTv,kahao;
        ImageView deleteGroupIv,touxiang;
    }


    public interface ItemDeleteButtonClickListener {

        void OnItemDeleteButtonClickListener(int position);

    }
}
