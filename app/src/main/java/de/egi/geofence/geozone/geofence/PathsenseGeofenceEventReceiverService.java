package de.egi.geofence.geozone.geofence;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.pathsense.android.sdk.location.PathsenseGeofenceEvent;

import org.apache.log4j.Logger;

import de.egi.geofence.geozone.R;
import de.egi.geofence.geozone.Worker;
import de.egi.geofence.geozone.db.DbGlobalsHelper;
import de.egi.geofence.geozone.utils.Constants;
import de.egi.geofence.geozone.utils.Utils;

/**
 * Created by egmont on 05.10.2016.
 */

public class PathsenseGeofenceEventReceiverService extends IntentService {
    private final Logger log = Logger.getLogger(PathsenseGeofenceEventReceiverService.class);


    public PathsenseGeofenceEventReceiverService() {
        super("PathsenseGeofenceEventReceiverService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int transition = 0;
        try {
            PathsenseGeofenceEvent geofenceEvent = PathsenseGeofenceEvent.fromIntent(intent);
            if (geofenceEvent != null) {
                if (geofenceEvent.isIngress()) {
                    // ingress
                    transition = Geofence.GEOFENCE_TRANSITION_ENTER;
                } else if (geofenceEvent.isEgress()) {
                    // egress
                    transition = Geofence.GEOFENCE_TRANSITION_EXIT;
                }

                String transitionType = getTransitionString(transition);

                // Post a notification
                // Notification senden
                DbGlobalsHelper dbGlobalsHelper = new DbGlobalsHelper(this);
                boolean reboot = Utils.isBoolean(dbGlobalsHelper.getCursorGlobalsByKey(Constants.DB_KEY_REBOOT));

                if (reboot) {
                    Log.i(Constants.APPTAG, "Do not call events after reboot or at update");
                    log.error("Do not call events after reboot or at update");
                    log.error("Reboot: " + true);
                    dbGlobalsHelper.storeGlobals(Constants.DB_KEY_REBOOT, "false");
                    return;
//                } else {
//                    NotificationUtil.sendNotification(this, transitionType, geofenceEvent.getGeofenceId(), Constants.FROM_PATHSENSE);
                }

                float accuracy = -1;
                if (geofenceEvent.getLocation().hasAccuracy()) {
                    accuracy = geofenceEvent.getLocation().getAccuracy();
                }

                // Requests ausführen
                // Aktionen ausführen
                Worker worker = new Worker(this.getApplicationContext());
                worker.handleTransition(transition, geofenceEvent.getGeofenceId(), Constants.GEOZONE, accuracy, geofenceEvent.getLocation(), Constants.FROM_PATHSENSE);

                // Log the transition type and a message
                Log.d(Constants.APPTAG, this.getString(R.string.geofence_transition_notification_title, transitionType, geofenceEvent.getGeofenceId()));
                Log.d(Constants.APPTAG, this.getString(R.string.geofence_transition_notification_text));
                log.info("after handleGeofenceTransition: " + this.getString(R.string.geofence_transition_notification_title, transitionType, geofenceEvent.getGeofenceId()));
                log.debug("after handleGeofenceTransition: " + this.getString(R.string.geofence_transition_notification_text));

            }
        }finally {
            // Release the wake lock provided by the WakefulBroadcastReceiver.
            log.debug("Release the wake lock");
            PathsenseGeofenceEventReceiver.completeWakefulIntent(intent);
        }
    }
    /**
     * Maps geofence transition types to their human-readable equivalents.
     * @param transitionType A transition type constant defined in Geofence
     * @return A String indicating the type of transition
     */
    private String getTransitionString(int transitionType) {
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                return this.getString(R.string.geofence_transition_entered);

            case Geofence.GEOFENCE_TRANSITION_EXIT:
                return this.getString(R.string.geofence_transition_exited);

            default:
                return this.getString(R.string.geofence_transition_unknown);
        }
    }

}
