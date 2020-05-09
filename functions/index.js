'use strict';
const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();

exports.sendNotification = functions.database.ref('/users/{username}')
        .onWrite(event => {


        //var payload = {
        //    data: {
        //        title: eventSnapshot.val()
        //    }
        //};

	console.log("ok");

        //return admin.messaging().sendToTopic(topic, payload)
        });