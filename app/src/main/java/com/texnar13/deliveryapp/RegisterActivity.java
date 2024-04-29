package com.texnar13.deliveryapp;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.texnar13.deliveryapp.model.Trip;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import io.realm.OrderedCollectionChangeSet;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.mongodb.App;
import io.realm.mongodb.AppConfiguration;
import io.realm.mongodb.Credentials;
import io.realm.mongodb.User;
import io.realm.mongodb.mongo.MongoClient;
import io.realm.mongodb.mongo.MongoCollection;
import io.realm.mongodb.mongo.MongoDatabase;
import io.realm.mongodb.sync.SyncConfiguration;

public class RegisterActivity extends AppCompatActivity {

    Realm uiThreadRealm;
    App app;

    public static final String MY_LOG = "My Log";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Realm.init(this);
        setContentView(R.layout.activity_register);






        // локальная БД
//        RealmConfiguration config = new RealmConfiguration.Builder()
//                .name("MyLocalDB3")
//                .allowWritesOnUiThread(true)
//                .allowQueriesOnUiThread(true)
//                .build();
//        uiThreadRealm = Realm.getInstance(config);
//        addChangeListenerToRealm(uiThreadRealm);


        // короче это, сук, тоже локальная бд
//        String realmFileName = "Delivery";
//        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
//                .name(realmFileName)
//                .allowWritesOnUiThread(true)
//                .allowQueriesOnUiThread(true)
//                .build();
//        Realm backgroundThreadRealm = Realm.getInstance(realmConfig);


        // подключение к онлайн бд
        String appID = "application-0-epiwn";


        App app = new App(new AppConfiguration.Builder(appID)
                .build());
        // авторизация
        Credentials apiKeyCredentials = Credentials.apiKey(
                "qwerty");
        AtomicReference<User> user = new AtomicReference<>();
        app.loginAsync(apiKeyCredentials, it -> {
            if (it.isSuccess()) {
                Log.e(MY_LOG, "Successfully authenticated using an API Key.");
                user.set(app.currentUser());
            } else {
                Log.e(MY_LOG, it.getError().toString());
            }
        });


        // Регистрация кодека для класса Trip
        //app.getConfiguration().getCodecRegistry().register(Trip.class);


        findViewById(R.id.register_button).setOnClickListener(view -> {


            MongoClient mongoClient = app.currentUser().getMongoClient("mongodb-atlas");
            MongoDatabase mongoDatabase = mongoClient.getDatabase("DeliveryApplicationDB");
            MongoCollection<Document> tripCollection = mongoDatabase.getCollection("Trip");
                    //.withCodecRegistry(pojoCodecRegistry);


            Log.e(MY_LOG, "TEST start");
            tripCollection.find().first().getAsync(result -> {
                Log.e(MY_LOG, "TEST isSuccess=" + result.isSuccess());
                if (result.isSuccess()) {
                    Document document = result.get();
                    Log.e(MY_LOG, "TEST all doc=" + document);
                    Log.e(MY_LOG, "TEST { _id=" + (ObjectId) document.get("_id"));
                    Log.e(MY_LOG, "TEST   страна_отбытия=" + (String) document.get("страна_отбытия"));
                } else {
                    Log.e(MY_LOG, "TEST error=" + result.getError());
                }
            });
            Log.e(MY_LOG, "TEST end");


            Log.e(MY_LOG, "TEST start");
            Document document = new Document();
            document.append("_id", new ObjectId());
            document.append("Id_trip", "test");
            document.append("страна_отбытия", "test страна_отбытия");
            document.append("город_отбытия", "test город_отбытия");
            document.append("страна_прибытия", "test страна_прибытия");
            document.append("город_прибытия", "test город_прибытия");
            document.append("дата_отбытия", new Date());
            document.append("транспорт", "test transport");

            tripCollection.insertOne(document).getAsync(result -> {
                Log.e(MY_LOG, "TEST isSuccess=" + result.isSuccess());
            });
            Log.e(MY_LOG, "TEST end");






//  TEST Trip=Document{
//      {
//          _id=661aeaf92cb807852acaa40e,
//          Id_trip=1,
//          страна_отбытия=Russia,
//          город_отбытия=Moscow,
//          страна_прибытия=Algeria,
//          город_прибытия=Laghouat,
//          дата_отбытия=Fri Mar 01 12:00:00 GMT+03:00 2024,
//          транспорт=Самолет, цена_поездки=2000 rouble
//      }
//  }



//            CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
//
//            List<Trip> list = new LinkedList<>();
//            CodecRegistry pojoCodecRegistry = CodecRegistries.fromRegistries(
//                    app.getConfiguration().getDefaultCodecRegistry(),
//                    CodecRegistries.fromProviders(pojoCodecProvider),
//                    CodecRegistries.fromRegistries(new CodecRegistry<Trip>)
//            );



//            backgroundThreadRealm.executeTransaction(realm -> {
//
//                RealmResults<Trip> trips = realm.where(Trip.class).findAll();
//                // Обработка полученных данных
//                Log.e(MY_LOG, "TEST " + trips.asJSON());
//
//
//            });


            //realm.getSchema().get("");
            //RealmCollection<Trip> tripsCollection =

            //"Trip", Trip.class);

//                        // Создание новой записи
//                        realm.executeTransactionAsync(transactionRealm -> {
//                            // Создание нового объекта Trip
//                            Trip newTrip = transactionRealm.createObject(Trip.class);
//                            newTrip.setDepartCountry("Russia");
//                            newTrip.setDepartCity("Moscow");
//                            newTrip.setArriveCountry("Algeria");
//                            newTrip.setArriveCity("Laghouat");
//                            newTrip.setDepartDate(new Date());
//                            newTrip.setTransport("Plane");
//                            newTrip.setCost("2000 rouble");
//
//                            // Другие поля могут быть установлены здесь
//
//                            // Добавление новой записи в коллекцию
//                            realm.insert(tripsCollection,newTrip);
//                        }, () -> {
//                            Log.v("MongoDB Realm", "New trip created successfully.");
//                        }, error -> {
//                            Log.e("MongoDB Realm", "Error creating new trip: " + error.getMessage());
//                        });


//            RealmResults<Trip> trips = backgroundThreadRealm.where(Trip.class).findAll();
//            Log.e(MY_LOG, trips.asJSON());

//            backgroundThreadRealm.executeTransaction(realm -> {
//                Log.e(MY_LOG, "Test");
//                RealmResults<Trip> trips = realm.where(Trip.class).findAll();
//                // Обработка полученных данных
//                Log.e(MY_LOG, trips.asJSON());
//            });

//            backgroundThreadRealm.executeTransaction(realm -> {
//                // Instantiate the class using the factory function.
//                Trip trip = realm.createObject(Trip.class, new ObjectId());
//                // Configure the instance.
//                trip.setArriveCity("hello");
//            });


//            // выход из учетки todo работает
//            app.currentUser().logOutAsync(result -> {
//                if (result.isSuccess()) {
//                    Log.v(MY_LOG, "Successfully logged out.");
//                } else {
//                    Log.e(MY_LOG, "Failed to log out, error: " + result.getError());
//                }
//            });


            //         todo работа с самой структурой бд, работает
//                RealmSchema shema = realm.getSchema();
//                Log.e(MY_LOG, "TEST " +
//                        shema.get("Trip").
//                );
            //.getFieldNames() ok .getPrimaryKey() (name)
        });

    }


    private void addChangeListenerToRealm(Realm realm) {
        // all tasks in the realm
        RealmResults<Trip> trips = realm.where(Trip.class).findAllAsync();
        trips.addChangeListener((collection, changeSet) -> {
            // process deletions in reverse order if maintaining parallel data structures so indices don't change as you iterate
            OrderedCollectionChangeSet.Range[] deletions = changeSet.getDeletionRanges();
            for (OrderedCollectionChangeSet.Range range : deletions) {
                Log.v(MY_LOG, "Deleted range: " + range.startIndex + " to " + (range.startIndex + range.length - 1));
            }
            OrderedCollectionChangeSet.Range[] insertions = changeSet.getInsertionRanges();
            for (OrderedCollectionChangeSet.Range range : insertions) {
                Log.v(MY_LOG, "Inserted range: " + range.startIndex + " to " + (range.startIndex + range.length - 1));
            }
            OrderedCollectionChangeSet.Range[] modifications = changeSet.getChangeRanges();
            for (OrderedCollectionChangeSet.Range range : modifications) {
                Log.v(MY_LOG, "Updated range: " + range.startIndex + " to " + (range.startIndex + range.length - 1));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // the ui thread realm uses asynchronous transactions, so we can only safely close the realm
        // when the activity ends and we can safely assume that those transactions have completed
        uiThreadRealm.close();
        app.currentUser().logOutAsync(result -> {
            if (result.isSuccess()) {
                Log.v(MY_LOG, "Successfully logged out.");
            } else {
                Log.e(MY_LOG, "Failed to log out, error: " + result.getError());
            }
        });
    }

//    public class BackgroundQuickStart implements Runnable {
//        User user;
//
//        public BackgroundQuickStart(User user) {
//            this.user = user;
//        }
//
//        @Override
//        public void run() {
//            String partitionValue = "My Project";
//            SyncConfiguration config = new SyncConfiguration.Builder(
//                    user,
//                    partitionValue)
//                    .allowQueriesOnUiThread(true)
//                    .allowWritesOnUiThread(true)
//                    .build();
//            Realm backgroundThreadRealm = Realm.getInstance(config);
//            Task task = new Task("New Task");
//            backgroundThreadRealm.executeTransaction(transactionRealm -> {
//                transactionRealm.insert(task);
//            });
//            // all tasks in the realm
//            RealmResults<Task> tasks = backgroundThreadRealm.where(Task.class).findAll();
//            // you can also filter a collection
//            RealmResults<Task> tasksThatBeginWithN = tasks.where().beginsWith("name", "N").findAll();
//            RealmResults<Task> openTasks = tasks.where().equalTo("status", TaskStatus.Open.name()).findAll();
//            Task otherTask = tasks.get(0);
//            // all modifications to a realm must happen inside of a write block
//            backgroundThreadRealm.executeTransaction(transactionRealm -> {
//                Task innerOtherTask = transactionRealm.where(Task.class).equalTo("_id", otherTask.get_id()).findFirst();
//                innerOtherTask.setStatus(TaskStatus.Complete);
//            });
//            Task yetAnotherTask = tasks.get(0);
//            ObjectId yetAnotherTaskId = yetAnotherTask.get_id();
//            // all modifications to a realm must happen inside of a write block
//            backgroundThreadRealm.executeTransaction(transactionRealm -> {
//                Task innerYetAnotherTask = transactionRealm.where(Task.class).equalTo("_id", yetAnotherTaskId).findFirst();
//                innerYetAnotherTask.deleteFromRealm();
//            });
//            // because this background thread uses synchronous realm transactions, at this point all
//            // transactions have completed and we can safely close the realm
//            backgroundThreadRealm.close();
//        }
//    }
}




