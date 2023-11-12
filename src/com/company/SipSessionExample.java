package com.company;

import javax.sip.*;
import javax.sip.address.*;
import javax.sip.header.*;
import javax.sip.message.*;
import java.util.Properties;

public class SipSessionExample {
    public static void main(String[] args) {
        try {
            // Створення SIP-фабрики та конфігурації
            SipFactory sipFactory = SipFactory.getInstance();
            sipFactory.setPathName("gov.nist");

            Properties properties = new Properties();
            properties.setProperty("javax.sip.IP_ADDRESS", "your_local_ip");
            properties.setProperty("javax.sip.STACK_NAME", "stack");

            SipStack sipStack = sipFactory.createSipStack(properties);
            SipProvider sipProvider = createSipProvider(sipStack);

            // Створення SIP-фабрики для адрес та заголовків
            AddressFactory addressFactory = sipFactory.createAddressFactory();
            MessageFactory messageFactory = sipFactory.createMessageFactory();
            HeaderFactory headerFactory = sipFactory.createHeaderFactory();

            // Створення SIP-адрес для зв'язку
            Address fromAddress = addressFactory.createAddress("sip:caller@yourdomain.com");
            Address toAddress = addressFactory.createAddress("sip:callee@remote_domain.com");

            // Створення SIP-запиту INVITE
            Request request = createInviteRequest(
                    toAddress.getURI(),
                    fromAddress,
                    toAddress,
                    headerFactory,
                    sipProvider
            );

            // Надсилання SIP-запиту
            ClientTransaction clientTransaction = sipProvider.getNewClientTransaction(request);
            clientTransaction.sendRequest();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static SipProvider createSipProvider(SipStack sipStack) throws PeerUnavailableException {
        ListeningPoint listeningPoint = sipStack.createListeningPoint("your_local_ip", 5060, "udp");
        SipProvider sipProvider = sipStack.createSipProvider(listeningPoint);
        sipProvider.addSipListener(new SipListenerImpl());
        return sipProvider;
    }

    private static Request createInviteRequest(
            URI toUri, Address fromAddress, Address toAddress,
            HeaderFactory headerFactory, SipProvider sipProvider) throws ParseException, SipException {

        CallIdHeader callIdHeader = sipProvider.getNewCallId();
        CSeqHeader cSeqHeader = headerFactory.createCSeqHeader(1, Request.INVITE);
        MaxForwardsHeader maxForwards = headerFactory.createMaxForwardsHeader(70);

        return sipProvider.getMessageFactory().createRequest(
                toUri,
                Request.INVITE,
                callIdHeader,
                cSeqHeader,
                fromAddress,
                toAddress,
                new ViaHeader(),
                maxForwards
        );
    }

    static class SipListenerImpl implements SipListener {
        // Реалізація методів обробки подій
    }
}