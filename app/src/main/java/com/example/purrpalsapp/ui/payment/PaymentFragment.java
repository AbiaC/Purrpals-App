package com.example.purrpalsapp.ui.payment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.purrpalsapp.R;
import com.example.purrpalsapp.ui.chat.MessageModal;
import com.example.purrpalsapp.ui.search.fragment_search;
import com.example.purrpalsapp.utils.LocalDataManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.stripe.android.view.CardInputWidget;

public class PaymentFragment extends Fragment {
    private static final String TAG = "PaymentFragment";

    private CardInputWidget cardInputWidget;
    private Button payButton, cancel;
    private TextView amountTextView;
    private TextView currencyTextView;
    private TextView paymentMethodTextView;
    private double amount;
    private PaymentModal mockPaymentGateway;
    private String payDate, payHours, payAmount, payStatus, payFrom, payTo, payId, payUserId, payCatId, payCatName, payCatBreed, payCatAge, payCatDescription, payCatImage, payCatStatus, payCatPrice, payCatRating, payCatAvailableFrom, payCatAvailableTo, ownerId;
    private DatabaseReference databaseReferencePayment, getDatabaseReferenceMessage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        cardInputWidget = view.findViewById(R.id.card_input_widget);
        payButton = view.findViewById(R.id.pay_button);
        amountTextView = view.findViewById(R.id.amount_text_view);
        currencyTextView = view.findViewById(R.id.currency_text_view);
        paymentMethodTextView = view.findViewById(R.id.payment_method_text_view);
        cancel = view.findViewById(R.id.cancel_button);
        databaseReferencePayment = FirebaseDatabase.getInstance().getReference("payments");
        getDatabaseReferenceMessage = FirebaseDatabase.getInstance().getReference("messages");

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment_search fragment2 = new fragment_search();
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment2).commit();
            }
        });
        Bundle bundle = getArguments();
        if (bundle != null) {
            amount = Double.parseDouble(bundle.getString("payAmount").toString());
            amountTextView.setText(String.format("Amount: $%.2f", amount));
            payDate = bundle.getString("payDate").toString();
            payHours = bundle.getString("payHours").toString();
            payAmount = bundle.getString("payAmount").toString();
            payStatus = bundle.getString("payStatus").toString();
            payFrom = bundle.getString("payFrom").toString();
            payTo = bundle.getString("payTo").toString();
            payId = bundle.getString("payId").toString();
            payUserId = bundle.getString("payUser").toString();
            payCatId = bundle.getString("payCat").toString();
            payCatName = bundle.getString("payName").toString();
            payCatBreed = bundle.getString("payBreed").toString();
            payCatAge = bundle.getString("payAge").toString();
            payCatDescription = bundle.getString("payDescription").toString();
            payCatImage = bundle.getString("payImage").toString();
            payCatStatus = bundle.getString("payStatus").toString();
            payCatPrice = bundle.getString("payPrice").toString();
            payCatRating = bundle.getString("payRating").toString();
            payCatAvailableFrom = bundle.getString("payFrom").toString();
            payCatAvailableTo = bundle.getString("payTo").toString();
            ownerId = bundle.getString("ownerId").toString();
            payStatus = bundle.getString("payStatus").toString();


        }
        mockPaymentGateway = new PaymentModal();

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePayment();
            }
        });

        return view;
    }

    private void makePayment() {

        // Hardcoded amount for demo purposes
        String currency = "USD"; // Hardcoded currency for demo purposes
        String paymentMethod = "Visa"; // Hardcoded payment method for demo purposes

        paymentMethodTextView.setText(String.format("Payment Method: %s", paymentMethod));
        currencyTextView.setText(String.format("Currency: %s", currency));
        amountTextView.setText(String.format("Amount: $%.2f", amount));

        LocalDataManager localDataManager = new LocalDataManager(requireActivity());
        String email = localDataManager.getEmail();

        Toast.makeText(getContext(), "Payment successful Updating Server !", Toast.LENGTH_SHORT).show();
        String pay = payId + "_" + ownerId + "_" + payCatId + "_" + System.currentTimeMillis();
        PaymentModal paymentModal = new PaymentModal(payDate, payHours, payAmount, payStatus, payFrom, payTo, pay, payUserId, payCatId, payCatName, payCatBreed, payCatAge, payCatDescription, payCatImage, payCatStatus, Float.parseFloat(payCatPrice), Float.parseFloat(payCatRating), payCatAvailableFrom, payCatAvailableTo, ownerId, email);
        databaseReferencePayment.child(pay).setValue(paymentModal);
        getDatabaseReferenceMessage.child(pay).push().setValue(new MessageModal(ownerId, true,
                "Payment Successful. Discuss About timing",
                System.currentTimeMillis(), FirebaseAuth.getInstance().getCurrentUser().getUid()));

        Toast.makeText(getContext(), "Server Updated Service Started !", Toast.LENGTH_SHORT).show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fragment_search fragment2 = new fragment_search();
                requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment2).commit();
            }
        }, 2500);
    }
}