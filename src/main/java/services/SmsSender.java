package services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;



public class SmsSender {

    public static void sendSms(String toPhoneNumber, String message) {
        Twilio.init(TwilioConfig.ACCOUNT_SID, TwilioConfig.AUTH_TOKEN);

        Message.creator(
                new PhoneNumber(toPhoneNumber),
                new PhoneNumber(TwilioConfig.TWILIO_PHONE_NUMBER),
                message
        ).create();
    }

}

