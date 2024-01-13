if (!this.InternetSpeed) {
    this.InternetSpeed = {};
}
(function() {
    'use strict';
    var internetSpeedContextPath = location.protocol + '//'
        + location.host + location.pathname;

    $(document).ready(function() {
         InternetSpeed.filter ();
    });
    InternetSpeed.filter = function() {
        $('#tbodyemployeeInternetSpeed').html(
            "<tr><td colspan='3' align='center'>Please wait...</td></tr>");
        $
            .ajax({
                url : internetSpeedContextPath + "/uploadUserInternetSpeed",
                type : 'GET',
                data : {
                    employeePid : $('#dbEmployee').val()

                },
                success : function(InternetSpeedDTOs) {
                    if (InternetSpeedDTOs.length == 0) {
                        $('#tbodyemployeeInternetSpeed')
                            .html(
                                "<tr><td colspan='2' align='center'>No data available</td></tr>");
                        return;
                    } else {
                        $('#tbodyemployeeInternetSpeed').html("");
                        $
                            .each(
                                InternetSpeedDTOs,
                                function(index, InternetSpeedDTO) {
                                console.log(InternetSpeedDTO);
                                    $(
                                        '#tbodyemployeeInternetSpeed')
                                        .append(
                                            "<tr><td>"
                                            +InternetSpeedDTO.username
                                            +"</td><td>"
                                            +InternetSpeedDTO.uploadSpeed
                                            +"</td><td>"
                                            +InternetSpeedDTO.downloadSpeed
                                            +"</td><td>"
                                            +InternetSpeedDTO.currentDateTime
                                            +"</td></tr>");

                                });

                    }
                }

            });
    }

})();