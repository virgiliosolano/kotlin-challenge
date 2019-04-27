package com.arctouch.codechallenge.util

import kotlin.reflect.KMutableProperty1

class MovieExtensions {

    String dateOfBirth = "26/02/1974";
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    Date date = null;
    try {
        date = sdf.parse(dateOfBirth);
    } catch (ParseException e) {
        // handle exception here !
    }

    String myString = DateFormat.getDateInstance(DateFormat.SHORT).format(date);


}