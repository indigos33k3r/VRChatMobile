package dahun.co.kr.vrchat_test;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class UserInfomationAdapter extends RecyclerView.Adapter<UserInfomationAdapter.UserInfomationViewHolder> {

    private ArrayList<UserInfomation> userData;
    private Context context;
    private int lastPosition = -1;
    SharedPreferences shared;
    SharedPreferences.Editor editor;

    class UserInfomationViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        ImageView thumnailImageView;
        TextView displayNameTextView;
        TextView locationTextView;
        LinearLayout itemLinearLayout;

        public UserInfomationViewHolder(View itemView) {
            super(itemView);
            thumnailImageView = itemView.findViewById(R.id.thumnail_imageView);
            displayNameTextView = itemView.findViewById(R.id.displayName_textView);
            locationTextView = itemView.findViewById(R.id.location_textView);
            itemLinearLayout = itemView.findViewById(R.id.item_layout);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, final View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            //contextMenu.setHeaderTitle("");
            // 밑 줄이 누른 곳에 써있는 displayName 가져오는법.
            //((TextView)(view.findViewById(R.id.displayName_textView))).getText().toString();
            Log.i("view_info", "" + ((TextView)(view.findViewById(R.id.displayName_textView))).getText().toString());
            contextMenu.add(0, view.getId(), 0, "메모 초기화").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    shared = context.getSharedPreferences(((TextView)(view.findViewById(R.id.displayName_textView))).getText().toString(), Context.MODE_PRIVATE);
                    editor = shared.edit();
                    editor.remove("sex");
                    editor.remove("vr");
                    editor.remove("vr");
                    editor.remove("memo");
                    editor.commit();
                    return true;
                }
            });//groupId, itemId, order, title
            contextMenu.add(1, view.getId(), 0, "친구삭제").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    // 친구를 삭제하는 코드를 입력하세요
                    return true;
                }
            });

            if (!DataUpdateThread.notificationList.contains(((TextView)(view.findViewById(R.id.displayName_textView))).getText().toString())) {
                contextMenu.add(2, view.getId(), 0, "접속 알림 켜기").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        DataUpdateThread.notificationList.add(((TextView)(view.findViewById(R.id.displayName_textView))).getText().toString());
                        // NotificationList에 displayName 집어넣기.
                        Log.i("notificationList", DataUpdateThread.notificationList.toString());
                        SharedPreferences.Editor editor = DataUpdateService.context.getSharedPreferences(DataUpdateThread.SETTING_SAVE, Context.MODE_PRIVATE).edit();
                        editor.putInt(DataUpdateService.id + "notificationList_size", DataUpdateThread.notificationList.size());
                        for (int i = 0; i < DataUpdateThread.notificationList.size(); i++) {
                            editor.putString(DataUpdateService.id + "notificationList" + i, DataUpdateThread.notificationList.get(i));
                        }
                        editor.commit();
                        return true;
                    }
                });
            }
            else {
                contextMenu.add(3, view.getId(), 0, "접속 알림 끄기").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        DataUpdateThread.notificationList.remove(((TextView)(view.findViewById(R.id.displayName_textView))).getText().toString());
                        // NotificationList에서 displayName 삭제하기
                        Log.i("notificationList", DataUpdateThread.notificationList.toString());
                        SharedPreferences.Editor editor = DataUpdateService.context.getSharedPreferences(DataUpdateThread.SETTING_SAVE, Context.MODE_PRIVATE).edit();
                        editor.putInt(DataUpdateService.id + "notificationList_size", DataUpdateThread.notificationList.size());
                        for (int i = 0; i < DataUpdateThread.notificationList.size(); i++) {
                            editor.putString(DataUpdateService.id + "notificationList" + i, DataUpdateThread.notificationList.get(i));
                        }
                        editor.commit();
                        return true;
                    }
                });
            }

        }


    }

    UserInfomationAdapter(ArrayList<UserInfomation> startData, Context context){
        userData = startData;
        Log.i("3.userData", "초기화 완료 : " + (userData == null?"null":"not null"));
        this.context = context;
    }

    @NonNull
    @Override
    public UserInfomationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.userinfomation_item, parent, false);
        return new UserInfomationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final UserInfomationViewHolder holder, final int position) {
        final UserInfomation showData = userData.get(position);


/*
        if (!holder.ImageURL.equals(showData.getImageURI())) {
            Log.i("thumnailImage_Reloading", "position : " + position + ", " + showData.getDisplayName() + " : " + holder.ImageURL + " -> " + showData.getImageURI());
            holder.ImageURL = showData.getImageURI();
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {    // 오래 거릴 작업을 구현한다
                    // TODO Auto-generated method stub
                    try {
                        URL url = new URL(showData.getImageURI());
                        InputStream is = url.openStream();
                        holder.bm = BitmapFactory.decodeStream(is);
                        holder.bm = getRoundedCornerBitmap(holder.bm, 100);
                        holder.thumnailImageView.setImageBitmap(holder.bm); //비트맵 객체로 보여주기

                    } catch (Exception e) {

                    }

                }
            });
            //holder.thumnailImageView.setImageBitmap(null);
            t.start();
        }
        else {
            //holder.thumnailImageView.setImageBitmap(holder.bm); //비트맵 객체로 보여주기
        }
        */
        holder.thumnailImageView.setImageBitmap(showData.getImageBitmap());
        holder.displayNameTextView.setText(showData.getDisplayName());
        holder.locationTextView.setText(showData.getLocation());

        holder.itemLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shared = context.getSharedPreferences(showData.getDisplayName(),Context.MODE_PRIVATE);
                editor = shared.edit();

                Log.i("SharedPreferences Task", context==null?"null":"not null");
                final Dialog dlg = new Dialog(context);
                dlg.setContentView(R.layout.memo_dialog);

                final RadioButton memo_rb_male = dlg.findViewById(R.id.memo_sexRadioButton_male);
                final RadioButton memo_rb_female = dlg.findViewById(R.id.memo_sexRadioButton_female);
                final RadioButton memo_rb_novr = dlg.findViewById(R.id.memo_vrRadioButton_novr);
                final RadioButton memo_rb_vr = dlg.findViewById(R.id.memo_vrRadioButton_vr);
                final EditText memo_EditText = dlg.findViewById(R.id.memo_editText);
                Button memo_cancelButton = dlg.findViewById(R.id.memo_cancelButton);
                Button memo_saveButton = dlg.findViewById(R.id.memo_saveButton);

                // 성별 불러오기
                if (shared.getString("sex","").equals("male"))
                    memo_rb_male.setChecked(true);
                else if (shared.getString("sex","").equals("female"))
                    memo_rb_female.setChecked(true);
                else {
                    memo_rb_male.setChecked(false);
                    memo_rb_female.setChecked(false);
                }

                // vr 불러오기
                if (shared.getString("vr","").equals("novr"))
                    memo_rb_novr.setChecked(true);
                else if (shared.getString("vr","").equals("vr"))
                    memo_rb_vr.setChecked(true);
                else {
                    memo_rb_novr.setChecked(false);
                    memo_rb_vr.setChecked(false);
                }

                // memo 불러오기
                memo_EditText.setText(shared.getString("memo",""));


                memo_cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dlg.dismiss();
                    }
                });

                memo_saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // 성별 저장
                        if (memo_rb_male.isChecked())
                            editor.putString("sex", "male");
                        else if (memo_rb_female.isChecked())
                            editor.putString("sex", "female");
                        else
                            editor.putString("sex", null);

                        // vr 저장
                        if (memo_rb_novr.isChecked())
                            editor.putString("vr", "novr");
                        else if (memo_rb_vr.isChecked())
                            editor.putString("vr", "vr");
                        else
                            editor.putString("vr", null);

                        //메모 저장
                        editor.putString("memo", memo_EditText.getText().toString());

                        // 커밋
                        editor.commit();

                        // 종료
                        dlg.dismiss();

                    }
                });

                dlg.show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return userData.size();
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }



    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }


}
