package it.uniba.di.sms1920.madminds.balanceout.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;

import it.uniba.di.sms1920.madminds.balanceout.R;

public class HomeFragment extends Fragment {

    private RecyclerView groupsRecyclerView;
    private GroupAdapter groupAdapter;
    private ArrayList<Group> groups;
    private FirebaseAuth mAuth;
    private boolean isLogged;

    private ImageView helpCardImageView;
    private SwipeRefreshLayout homeSwipeRefresh;
    private FloatingActionButton addGroupFab;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        groupsRecyclerView = root.findViewById(R.id.groupsHomeRecyclerView);
        helpCardImageView = root.findViewById(R.id.helpCardImageView);
        homeSwipeRefresh = root.findViewById(R.id.homeSwipeRefresh);
        addGroupFab = root.findViewById(R.id.homeExpandableFab);

        groups = new ArrayList<>();

        /* funzione che verifica se l'utente è loggato o meno e memorizza l'informazione in isLogged*/
        verifyLogged();

        /* vengono caricati tutti i gruppi nella recycle view */
        loadGroups();

        /* messaggio di aiuto per comprendere il significato della card relativa a stato debiti/crediti*/
        helpCardImageView.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v){
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle(getString(R.string.title_help_status_debit))
                        .setMessage(getString(R.string.text_help_status_debit))
                        .setPositiveButton(getString(R.string.understand), null)
                        .show();
            }
        });

        addGroupFab.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View v){
                if(!isLogged) {
                    Snackbar.make(root, getString(R.string.not_logged_message_add_group), Snackbar.LENGTH_LONG).show();
                }
            }
        });


        /* quando viene ricaricata la pagina con uno swipe down, vengono ricaricati tutti i gruppi*/
        homeSwipeRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loadGroups();
                    }
                }
        );

        return root;
    }

    public void loadGroups() {
        /* la lista viene pulita poiche altrimenti ogni volta ce si ricarica la pagina
        *  verrebbero aggiunti gli stessi gruppi */
        groups.clear();

        if(!isLogged) {
            /*creazione di un gruppo di esempio visibile solo quando l'utente non è loggato*/
            groups.add(new Group(getString(R.string.example_name_group),
                    Calendar.getInstance().getTime(),
                    null,
                    -1
            ));
        }

        groupAdapter = new GroupAdapter(groups,isLogged, getActivity());

        groupsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        groupsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        groupsRecyclerView.setAdapter(groupAdapter);

        homeSwipeRefresh.setRefreshing(false);
    }

    private void verifyLogged() {
        /* firebaseUser contiene l'informazione relativa all'utente se è loggato o meno */
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        /* memorizzo in isLogged l'informazione boolean relativa all'utente se è loggato o meno*/
        if(firebaseUser == null) {
            isLogged = false;
        } else {
            isLogged = true;
        }
    }
}