package com.sportsclub.baseballqld;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sportsclub.baseballqld.models.Event;
import com.sportsclub.baseballqld.models.File;
import com.sportsclub.baseballqld.models.Folder;
import com.sportsclub.baseballqld.models.Group;
import com.sportsclub.baseballqld.models.GroupFolders;
import com.sportsclub.baseballqld.models.GroupFoldersRes;

import java.util.Vector;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DocumentsVC extends Fragment {

    public Group group;

    public Folder rootFolder = null;
    public GroupFolders groupRootFolder = null;

    private ListView listView;
    private ArrayAdapter<Event> listAdapter;
    private SwipeRefreshLayout refreshLayout;
    private Button backButton;
    private TextView mainTV;
    private ImageView emptyIV;

    private Vector<Folder> backFolders = new Vector<Folder>();

    public DocumentsVC() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(false);

        // Inflate the layout for this fragment
        inflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.fragment_documents_vc, container, false);
        emptyIV = v.findViewById(R.id.empty);

        backButton = v.findViewById(R.id.button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(backFolders.size() >0 )
                {
                    rootFolder = backFolders.lastElement();
                    backFolders.removeElementAt(backFolders.size()-1);

                    if(backFolders.size() ==0)
                    {
                        // rootFolder = null;

                    }
                    Log.d("hq","size is just now "+backFolders.size());
                }
                else
                {
                    Log.d("hq","size is zero");
                    rootFolder = null;

                }
                modelToView();
            }
        });

        mainTV = v.findViewById(R.id.textView);

        listView = v.findViewById(R.id.list);
        listAdapter= new ArrayAdapter<Event>(this.getContext(), R.layout.document_cell){


            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {

                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.document_cell, parent, false);
                }

                Folder f = null;
                File file = null;

                if(rootFolder !=null)
                {
                    int numFolders = rootFolder.childFolders.size();
                    if(position >= numFolders)
                    {
                        //we have a file
                        file = rootFolder.files.get(position-numFolders);
                    }
                    else
                    {
                        //we have a folder
                        f = rootFolder.childFolders.get(position);
                    }
                }
                else if(groupRootFolder != null)
                {
                    f = groupRootFolder.folders.get(position);
                }
                else
                {

                }

                TextView firstTV = convertView.findViewById(R.id.textView);
                ImageView iv = convertView.findViewById(R.id.imageView);
                ImageView editIV = convertView.findViewById(R.id.editIV);

                if(f !=null)
                {
                    firstTV.setText(f.folderName);
                    iv.setImageDrawable(getResources().getDrawable(R.drawable.folder));
                    editIV.setVisibility(View.GONE);

                }

                if(file!=null)
                {
                    firstTV.setText(""+file.fileName+ " ("+file.fileDescription+")");
                    iv.setImageDrawable(getResources().getDrawable(R.drawable.filetext_75));
                    editIV.setVisibility(View.GONE);
                }

                final Folder chosenFolder = f;
                final File chosenFile = file;
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if(chosenFolder !=null)
                        {
                            if(rootFolder!=null) backFolders.add(rootFolder);
                            rootFolder = chosenFolder;

                        }


                        if(chosenFile !=null)
                        {
                            //open file
                             Toast.makeText(getActivity(),"Open File "+chosenFile.fileName,Toast.LENGTH_LONG).show();

                            //USE google opener! http://stackoverflow.com/questions/4947591/open-a-pdf-file-inside-a-webview

                            String fileURL = "http://docs.google.com/gview?embedded=true&url="+chosenFile.url;

                            WebVC.url = fileURL;
                            WebVC.title = chosenFile.fileName;
                            Intent i = new Intent(DocumentsVC.this.getActivity(), WebVC.class);
                            startActivity(i);

                            /*PDFVC.url = chosenFile.url;
                            Intent i = new Intent(DocumentsVC.this.getContext(), PDFVC.class);
                            startActivity(i);*/

                        }

                        modelToView();

                    }
                });

                editIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editFolderAction(chosenFolder);
                    }
                });

                return convertView;
            }

            @Override
            public int getCount() {

                if(rootFolder != null)
                {
                    return rootFolder.childFolders.size() + rootFolder.files.size();
                }

                else if(groupRootFolder != null)
                {
                    return groupRootFolder.folders.size();
                }
                else return 0;
            }
        };
        listView.setAdapter(listAdapter);

        refreshLayout = v.findViewById(R.id.swiperefresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                loadData();
            }
        });

        return v;
    }



    private void modelToView()
    {
        if(rootFolder !=null)
        {
            backButton.setVisibility(View.VISIBLE);
            mainTV.setText("Listing for: " + rootFolder.folderName);
        }
        else if(groupRootFolder != null)
        {
            backButton.setVisibility(View.INVISIBLE);
            mainTV.setText("Listing for: " + groupRootFolder.groupName);
        }
        else
        {
            mainTV.setText("");
        }

        listAdapter.notifyDataSetChanged();
    }

    // NEEDED with configChange in manifest, stops view changer from recalling onCreateView
    private boolean initialLoaded = false;
    public void loadIfUnloaded()
    {
        if(initialLoaded == false) loadData();
    }

    private void loadData()
    {
        initialLoaded = true;
        Log.d("hq","groupID: "+group.groupId);
        final ProgressDialog pd = DM.getPD(this.getContext(),"Loading Folders...");
        pd.show();


        DM.getApi().getGroupFoldersnew(DM.getAuthString(), group.groupId, new Callback<GroupFoldersRes>() {
            @Override
            public void success(GroupFoldersRes gfs, Response response) {

                try {
                    groupRootFolder = gfs.getData();
                    rootFolder=null;
                    backFolders = new Vector<Folder>();
                    modelToView();
                    pd.dismiss();
                    refreshLayout.setRefreshing(false);

                    if((gfs.getData()).folders.size()==0) emptyIV.setVisibility(View.VISIBLE);
                    else emptyIV.setVisibility(View.GONE);
                }

                catch (NullPointerException n){
                    n.printStackTrace();
                    Log.d("hp", "error: " +n);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                pd.dismiss();
                refreshLayout.setRefreshing(false);
                Toast.makeText(getContext(),"Could not load documents "+error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {


        inflater.inflate(R.menu.create_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if(item.getItemId() == R.id.create) this.newFolderAction();

        return super.onOptionsItemSelected(item);
    }

    private void newFolderAction()
    {
        Log.d("hq","new folder click!");

        AlertDialog.Builder alert = new AlertDialog.Builder(this.getContext());

        final EditText edittext = new EditText(this.getActivity());
        String message = "Enter your folder name";
        if(rootFolder != null) message += " it will be created as a subfolder of: "+rootFolder.folderName;

        alert.setMessage(message);
        alert.setTitle("Create Folder");

        alert.setView(edittext);

        alert.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String name = edittext.getText().toString();
                Log.d("hq","create folder with name:"+name);

                Folder f = new Folder();
                f.folderName = name;
                if(rootFolder != null )f.parentFolderId = rootFolder.folderId;
                else f.parentFolderId = null;

                f.groupId = group.groupId;


                DM.getApi().postFolder(DM.getAuthString(), f, new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {


                        Toast.makeText(DocumentsVC.this.getActivity(), "Folder Created!", Toast.LENGTH_LONG).show();
                        loadData();
                        DM.hideKeyboard(DocumentsVC.this.getActivity());

                    }

                    @Override
                    public void failure(RetrofitError error) {

                        Toast.makeText(DocumentsVC.this.getActivity(), "Could not create folder:"+error.getMessage(), Toast.LENGTH_LONG).show();
                        DM.hideKeyboard(DocumentsVC.this.getActivity());
                    }
                });
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        alert.show();
    }

    private void editFolderAction(final Folder editFolder)
    {
        Log.d("hq","edit folder click!");

        AlertDialog.Builder alert = new AlertDialog.Builder(this.getContext());

        final EditText edittext = new EditText(this.getActivity());
        edittext.setText(editFolder.folderName);
        String message = "Enter the new folder name";
        alert.setMessage(message);
        alert.setTitle("Edit Folder");
        alert.setView(edittext);

        alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String name = edittext.getText().toString();
                Log.d("hq","update folder with name:"+name);

                editFolder.folderName = name;



                /*DM.getApi().putFolder(DM.getAuthString(), editFolder, new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {

                        Toast.makeText(DocumentsVC.this.getActivity(), "Folder Updated!", Toast.LENGTH_LONG).show();
                        modelToView();
                        DM.hideKeyboard(DocumentsVC.this.getActivity());

                    }

                    @Override
                    public void failure(RetrofitError error) {

                        Toast.makeText(DocumentsVC.this.getActivity(), "Could not update folder:"+error.getMessage(), Toast.LENGTH_LONG).show();
                        DM.hideKeyboard(DocumentsVC.this.getActivity());
                    }
                });*/
                DM.getApi().putFolders(DM.getAuthString(), editFolder, new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {

                        Toast.makeText(DocumentsVC.this.getActivity(), "Folder Updated!", Toast.LENGTH_LONG).show();
                        modelToView();
                        DM.hideKeyboard(DocumentsVC.this.getActivity());

                    }

                    @Override
                    public void failure(RetrofitError error) {

                        Toast.makeText(DocumentsVC.this.getActivity(), "Could not update folder:"+error.getMessage(), Toast.LENGTH_LONG).show();
                        DM.hideKeyboard(DocumentsVC.this.getActivity());
                    }
                });
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
            }
        });

        alert.show();

    }

}
