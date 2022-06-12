package com.ezcorporate.Firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.ezcorporate.Assignments.AssignmentBoard;
import com.ezcorporate.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String NOTIFICATION_ID_EXTRA = "notificationId";
    private static final String IMAGE_URL_EXTRA = "imageUrl";
    private static final String ADMIN_CHANNEL_ID ="admin_channel";
    private NotificationManager notificationManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.i("firebaserunning","message running");
        Intent notificationIntent = new Intent(this, AssignmentBoard.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,notificationIntent,PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notifyBuilder=new NotificationCompat.Builder(this);
        notifyBuilder.setContentTitle("FCM Notification");
        notifyBuilder.setAutoCancel(true);
        notifyBuilder.setContentText("sdsdsd");
        NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,notifyBuilder.build());

//        if(AssignmentBoard.isAppRunning){
//            //Some action
//            Toast.makeText(MyFirebaseMessagingService.this,"firebase running",Toast.LENGTH_SHORT).show();
//        }else{
//            //Show notification as usual
//        }

//        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        final PendingIntent pendingIntent = PendingIntent.getActivity(this,
//                0 /* Request code */, notificationIntent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        //You should use an actual ID instead
//        int notificationId = new Random().nextInt(60000);
//
//        Bitmap bitmap = getBitmapfromUrl(remoteMessage.getData().get("image-url"));
//
//        Intent likeIntent = new Intent(this,LikeService.class);
//        likeIntent.putExtra(NOTIFICATION_ID_EXTRA,notificationId);
//        likeIntent.putExtra(IMAGE_URL_EXTRA,remoteMessage.getData().get("image-url"));
//        PendingIntent likePendingIntent = PendingIntent.getService(this,
//                notificationId+1,likeIntent,PendingIntent.FLAG_ONE_SHOT);
//
//
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//        notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            setupChannels();
//        }
//
//            NotificationCompat.Builder notificationBuilder =
//                    new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
//                    .setLargeIcon(bitmap)
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setContentTitle(remoteMessage.getData().get("title"))
//                    .setStyle(new NotificationCompat.BigPictureStyle()
//                            .setSummaryText(remoteMessage.getData().get("message"))
//                            .bigPicture(bitmap))/*Notification with Image*/
//                    .setContentText(remoteMessage.getData().get("message"))
//                    .setAutoCancel(true)
//                    .setSound(defaultSoundUri)
//                    .addAction(R.drawable.ic_favorite_true,
//                            getString(R.string.notification_add_to_cart_button),likePendingIntent)
//                    .setContentIntent(pendingIntent);
//
//            notificationManager.notify(notificationId, notificationBuilder.build());

    }

//    public Bitmap getBitmapfromUrl(String imageUrl) {
//        try {
//            URL url = new URL(imageUrl);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setDoInput(true);
//            connection.connect();
//            InputStream input = connection.getInputStream();
//            return BitmapFactory.decodeStream(input);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private void setupChannels(){
//        CharSequence adminChannelName = getString(R.string.notifications_admin_channel_name);
//        String adminChannelDescription = getString(R.string.notifications_admin_channel_description);
//
//        NotificationChannel adminChannel;
//        adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_LOW);
//        adminChannel.setDescription(adminChannelDescription);
//        adminChannel.enableLights(true);
//        adminChannel.setLightColor(Color.RED);
//        adminChannel.enableVibration(true);
//        if (notificationManager != null) {
//            notificationManager.createNotificationChannel(adminChannel);
//        }
//    }
}