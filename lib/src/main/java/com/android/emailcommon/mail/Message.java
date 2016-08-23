/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.emailcommon.mail;

import java.util.Date;

public abstract class Message implements Part, Body {
    public static final Message[] EMPTY_ARRAY = new Message[0];

    public enum RecipientType {
        TO, CC, BCC,
    }

    protected String mUid;

    protected Date mInternalDate;

    public String getUid() {
        return mUid;
    }

    public void setUid(String uid) {
        this.mUid = uid;
    }

    /*public Folder getFolder() {
        return mFolder;
    }*/

    public abstract String getSubject() throws MessagingException;

    public abstract void setSubject(String subject) throws MessagingException;

    public Date getInternalDate() {
        return mInternalDate;
    }

    public void setInternalDate(Date internalDate) {
        this.mInternalDate = internalDate;
    }

    public abstract Date getReceivedDate() throws MessagingException;

    public abstract Date getSentDate() throws MessagingException;

    public abstract void setSentDate(Date sentDate) throws MessagingException;

    public abstract Address[] getRecipients(RecipientType type) throws MessagingException;

    public abstract void setRecipients(RecipientType type, Address[] addresses)
            throws MessagingException;

    public void setRecipient(RecipientType type, Address address) throws MessagingException {
        setRecipients(type, new Address[] {
            address
        });
    }

    public abstract Address[] getFrom() throws MessagingException;

    public abstract void setFrom(Address from) throws MessagingException;

    public abstract Address[] getReplyTo() throws MessagingException;

    public abstract void setReplyTo(Address[] from) throws MessagingException;

    // Always use these instead of getHeader("Message-ID") or setHeader("Message-ID");
    public abstract void setMessageId(String messageId) throws MessagingException;
    public abstract String getMessageId() throws MessagingException;

    @Override
    public boolean isMimeType(String mimeType) throws MessagingException {
        return getContentType().startsWith(mimeType);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ':' + mUid;
    }
}
