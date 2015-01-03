package circlebinder.creation.initialize;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import circlebinder.common.Legacy;
import circlebinder.common.table.SQLite;

public final class DatabaseInitializeService extends Service implements Legacy {

    private final RemoteCallbackList<IInitializeServiceCallback> callbacks = new RemoteCallbackList<>();

    private IInitializeBindService.Stub stub = new IInitializeBindService.Stub() {

        @Override
        public void setObserver(IInitializeServiceCallback callback) throws RemoteException {
            callbacks.register(callback);
        }

        @Override
        public void removeObserver(IInitializeServiceCallback callback) throws RemoteException {
            callbacks.unregister(callback);
        }

        @Override
        public void start() throws RemoteException {
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    new CreationDatabaseInitializer(getApplicationContext())
                            .initialize(SQLite.getWritableDatabase(getApplicationContext()));
                    callback();
                }
            });
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    private void callback() {
        int n = callbacks.beginBroadcast();
        for (int i=0; i<n; i++) {
            try {
                callbacks.getBroadcastItem(i).initializeCompleted();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        callbacks.finishBroadcast();
    }

}
