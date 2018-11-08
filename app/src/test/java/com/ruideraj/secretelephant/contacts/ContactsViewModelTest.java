package com.ruideraj.secretelephant.contacts;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.MutableLiveData;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;
import com.ruideraj.secretelephant.AccountManager;
import com.ruideraj.secretelephant.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ContactsViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private ContactsRepository mContactsRepository;

    @Mock
    private AccountManager mAccountManager;

    @Mock
    private Task<GoogleSignInAccount> mFakeTask;

    @Mock
    private GoogleSignInAccount mFakeAccount;

    private ContactsViewModel mContactsViewModel;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        when(mContactsRepository.getPhonesData()).thenReturn(new MutableLiveData<>());
        when(mContactsRepository.getEmailsData()).thenReturn(new MutableLiveData<>());



        mContactsViewModel = new ContactsViewModel(mContactsRepository, mAccountManager);
    }

    @Test
    public void progressVisible() {
        assertNotNull(mContactsViewModel.showSelection.getValue());
        assertEquals(View.GONE, (int) mContactsViewModel.showSelection.getValue());
        assertNotNull(mContactsViewModel.showProgress.getValue());
        assertEquals(View.VISIBLE, (int) mContactsViewModel.showProgress.getValue());
    }

    @Test
    public void selectionVisible() {
        mContactsViewModel.phones.setValue(new ArrayList<>());
        assertNotNull(mContactsViewModel.showSelection.getValue());
        assertNotNull(mContactsViewModel.showProgress.getValue());
        assertEquals(View.VISIBLE, (int) mContactsViewModel.showSelection.getValue());
        assertEquals(View.GONE, (int) mContactsViewModel.showProgress.getValue());
    }

    @Test
    public void signInSuccess() {
        String email = "fake@email.com";
        when(mFakeTask.getResult(any())).thenReturn(mFakeAccount);
        when(mFakeAccount.getEmail()).thenReturn(email);

        mContactsViewModel.signInResult(mFakeTask);
        assertEquals(email, mContactsViewModel.emailAccount.getValue());
    }

    @Test
    public void signInError() {
        Status errorStatus = new Status(GoogleSignInStatusCodes.SIGN_IN_FAILED, "sign in failed!");
        when(mFakeTask.getResult(any())).thenThrow(new ApiException(errorStatus));

        mContactsViewModel.signInResult(mFakeTask);
        assertNotNull(mContactsViewModel.toast.getValue());
        assertEquals(R.string.contact_google_sign_in_error, (int) mContactsViewModel.toast.getValue());
    }
}
