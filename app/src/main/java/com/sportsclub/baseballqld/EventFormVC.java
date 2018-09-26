package com.sportsclub.baseballqld;

import android.app.DatePickerDialog;
import android.app.Service;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.sportsclub.baseballqld.models.Event;
import com.sportsclub.baseballqld.models.Group;
import com.sportsclub.baseballqld.models.GroupResponse;
import com.sportsclub.baseballqld.views.SelectGroupDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EventFormVC extends BaseVC {

    private static final String TAG = "Location";
    //Model
    public static Event event;
    private final int PLACE_PICKER_REQUEST = 1;
    private List<Group> userGroups = null;
    //VIEWS
    private EditText nameET;
    private Button startDate;
    private Button startTime;
    private Button endDate;
    private Button endTime;
    private Button location;
    private EditText notesET;
    private TextView tv_event_title;
    private LinearLayout ll_back, ll_save;
    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_event_form_sc);


        if (event == null) {
            event = new Event();
        }


        tv_event_title = findViewById(R.id.tv_event_title);
        tv_event_title.setText("Update Event");
        if (event.eventName == null) {
            tv_event_title.setText("New Event");
        }

        ll_back = findViewById(R.id.ll_back);

        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        ll_save = findViewById(R.id.ll_edit);

        ll_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAction();
            }
        });

        nameET = findViewById(R.id.nameET);
        startDate = findViewById(R.id.startDate);
        startTime = findViewById(R.id.startTime);
        endDate = findViewById(R.id.endDate);
        endTime = findViewById(R.id.endTime);
        location = findViewById(R.id.location);
        notesET = findViewById(R.id.notesET);


        final Calendar c = Calendar.getInstance();
        final int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH);
        final int day = c.get(Calendar.DAY_OF_MONTH);

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DatePickerDialog d = new DatePickerDialog(EventFormVC.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        if (event.eventStart == null) {
                            event.eventStart = new Date();
                        }

                        event.eventStart.setYear(year - 1900);//gregorian
                        event.eventStart.setMonth(monthOfYear);
                        event.eventStart.setDate(dayOfMonth);

                        startDate.setText("Start Date: " + DM.getDateOnlyString(event.eventStart));


                    }
                }, year, month, day);

                d.show();
            }
        });

        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                TimePickerDialog t = new TimePickerDialog(EventFormVC.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {


                        if (event.eventStart == null) {
                            event.eventStart = new Date();
                        }

                        event.eventStart.setHours(hourOfDay);
                        event.eventStart.setMinutes(minute);
                        startTime.setText("Start Time: " + DM.getTimeOnlyString(event.eventStart));

                    }
                }, mHour, mMinute, false);
                t.show();


            }
        });


        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog d = new DatePickerDialog(EventFormVC.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        if (event.eventEnd == null) {
                            event.eventEnd = new Date();
                        }


                        event.eventEnd.setYear(year - 1900);//gregorian
                        event.eventEnd.setMonth(monthOfYear);
                        event.eventEnd.setDate(dayOfMonth);

                        endDate.setText("End Date: " + DM.getDateOnlyString(event.eventEnd));
                    }
                }, year, month, day);
                d.show();
            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                TimePickerDialog t = new TimePickerDialog(EventFormVC.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        if (event.eventEnd == null) {
                            event.eventEnd = new Date();
                        }

                        event.eventEnd.setHours(hourOfDay);
                        event.eventEnd.setMinutes(minute);

                        endTime.setText("End Time: " + DM.getTimeOnlyString(event.eventEnd));

                    }
                }, mHour, mMinute, false);
                t.show();
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                launchLocationPicker();

            }
        });


        DM.getApi().getAllGrouping(DM.getAuthString(), new Callback<GroupResponse>() {
            @Override
            public void success(GroupResponse gs, Response response) {

                userGroups = gs.getData();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(EventFormVC.this, "Could not load group members", Toast.LENGTH_SHORT).show();
            }
        });


        this.modelToView();

    }

    private void launchLocationPicker() {

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            Intent i = builder.build(this);
            startActivityForResult(i, PLACE_PICKER_REQUEST);

        } catch (Exception e) {
            Toast.makeText(this, "Could not load map picker, try again later", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

                event.location = place.getName().toString();
                event.latitude = place.getLatLng().latitude;
                event.longitude = place.getLatLng().longitude;

                location.setText("Location: " + event.location);
            }
        }
    }

    private void modelToView() {
        if (event.eventId == 0) {
            setTitle("Create Event");
            startDate.setText("Select Start Date");
            startTime.setText("Select Start Time");
            endDate.setText("Select End Date");
            endTime.setText("Select End Time");
            location.setText("Choose Location");
        } else {
            setTitle("Save Event");
            nameET.setText(event.eventName);

            startDate.setText("Start Date: " + DM.getDateOnlyString(event.eventStart));
            startTime.setText("Start Time: " + DM.getTimeOnlyString(event.eventStart));
            endDate.setText("End Date: " + DM.getDateOnlyString(event.eventEnd));
            endTime.setText("End Time: " + DM.getTimeOnlyString(event.eventEnd));


            notesET.setText(event.notes);
            location.setText("Location: " + event.location);
        }
    }

    private void saveAction() {

        //view to model
        event.eventName = nameET.getText().toString();
        event.notes = notesET.getText().toString();
        event.AddNotificationToGroupHome = true;

        if (event.eventName.equals("")) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
            return;
        }

        if (event.eventStart == null || event.eventEnd == null) {
            Toast.makeText(this, "Please select dates", Toast.LENGTH_LONG).show();
            return;
        }

        if (event.longitude == 0 && event.latitude == 0) {
            Toast.makeText(this, "Please select a location with valid coordinates", Toast.LENGTH_LONG).show();
            return;
        }

        if (userGroups == null || userGroups.size() == 0) {
            Toast.makeText(this, "User has no group to save to! join a group or try again alter", Toast.LENGTH_LONG).show();
            return;
        }

        if (event.notes.equals("") || notesET.getText().toString().isEmpty()) {
            Toast toast = Toast.makeText(this, "Notes are empty!!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            notesET.requestFocus();
            notesET.setFocusable(true);
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Service.INPUT_METHOD_SERVICE);
            imm.showSoftInput(notesET, 0);
            return;
        }

        Log.d("hq", "Start: " + DM.getDateOnlyString(event.eventStart) + "-" + DM.getTimeOnlyString(event.eventStart));

        if (event.eventId == 0) {
            SelectGroupDialog sgd = new SelectGroupDialog(this, userGroups, new SelectGroupDialog.Protocol() {
                @Override
                public void didSelectGroup(Group group) {
                    event.familyId = group.groupId;
                    networkPost();

                }
            });
            sgd.show();
        } else //UPDATE
        {
            //family ID already defined (hopefully anyway)
            networkPut();
        }
    }

    private void networkPut() {
        /*DM.getApi().putEvent(DM.getAuthString(), event, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Toast.makeText(EventFormVC.this,"Event Updated!",Toast.LENGTH_LONG).show();
                EventsVC.oneShotRefresh = true; //reload events...
                EventFormVC.this.finish();
            }

            @Override
            public void failure(RetrofitError error) {

                Toast.makeText(EventFormVC.this,"Could not update event: "+error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });*/

        DM.getApi().putEvents(DM.getAuthString(), event, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Toast.makeText(EventFormVC.this, "Event Updated..", Toast.LENGTH_SHORT).show();
                EventsFragment.oneShotRefresh = true; //reload events...
                //EventFormVC.this.finish();
                EventFormVC.this.finish();

            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(EventFormVC.this, "Could not update event: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void networkPost() {

        DM.getApi().postEvents(DM.getAuthString(), event, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Toast.makeText(EventFormVC.this, "Event Created!", Toast.LENGTH_LONG).show();
                EventsFragment.oneShotRefresh = true; //reload events...
                EventFormVC.this.finish();


            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("hq", error.getMessage());
                Toast.makeText(EventFormVC.this, "Could not create event: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onDestroy() {
        event = null;
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.save) this.saveAction();

        return super.onOptionsItemSelected(item);
    }
}

