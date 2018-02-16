package solet;

/**
 * Created by Todor Popov using Lenovo on 13.2.2018 г. at 23:16.
 */
public interface HttpSolet {
    void doGet(HttpSoletRequest request, HttpSoletResponse response);

    void doPost(HttpSoletRequest request, HttpSoletResponse response);

    void doPut(HttpSoletRequest request, HttpSoletResponse response);

    void doDelete(HttpSoletRequest request, HttpSoletResponse response);
}
