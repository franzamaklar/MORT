package com.masters.mort.ui.main.report.utilities

import android.content.ContentValues
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.nio.file.Path
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

class EmailSender {
    companion object{
        fun sendEmail(tempFile: Path, userEmail: String){
            try {
                val properties = Properties()
                properties["mail.smtp.auth"] = "true"
                properties["mail.smtp.host"] = "smtp.gmail.com"
                properties["mail.smtp.port"] = 587
                properties["mail.smtp.starttls.enable"] = "true"
                properties["mail.transport.protocol"] = "smtp"

                val session = Session.getInstance(properties, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication("mort.testings@gmail.com", "syzprecsyufrdxqy")
                    }
                })

                val message: Message = MimeMessage(session)
                message.subject = "Email from MORT Application"

                val addressTo: Address = InternetAddress("mort.testings@gmail.com")
                message.setRecipient(Message.RecipientType.TO, addressTo)

                val multipart = MimeMultipart()
                val attachment = MimeBodyPart()
                attachment.attachFile(File(tempFile.toUri()))

                val messageBody = MimeBodyPart()
                messageBody.setContent("<h1>Mortality statistic from database by user email: ${userEmail}</h1>", "text/html")
                multipart.addBodyPart(messageBody)
                multipart.addBodyPart(attachment)

                message.setContent(multipart)

                CoroutineScope(Dispatchers.IO).launch {
                    Transport.send(message)
                }
            } catch (exception: Exception) {
                exception.message?.let { Log.e(ContentValues.TAG, it) }
            }
        }
    }
}