<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <%@ taglib tagdir="/WEB-INF/tags/util" prefix="util" %>

            <html lang="en">

            <head>
                <jsp:include page="../fragments/m_head.jsp"></jsp:include>
                <title>SalesNrich | Sync Uncle Jhon Masters</title>
            </head>

            <body class="page-body" data-url="">
                <div class="page-container">
                    <jsp:include page="../fragments/m_navbar.jsp"></jsp:include>
                    <div class="main-content">
                        <jsp:include page="../fragments/m_header_main.jsp"></jsp:include>
                        <hr />
                        <h2>Sync Masters</h2>

                        <div class="row">
                            <hr />

                            <div class="col-md-12 col-sm-12 clearfix">
                                <form role="form" class="form-horizontal form-groups-bordered">
                                    <div class="row">
                                        <div class="col-sm-3">
                                            <br>
                                            <button type="button" class="btn btn-success " id="uploadAll"
                                                style="width: 175px; text-align: center;">Upload
                                                Masters</button>
                                        </div>
                                        <div class="col-md-9">
                                            <div class="panel-body">
                                                <div class="form-group">
                                                    <label class="error-msg"
                                                        style="color: red; text-align: center;"></label>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </form>
                            </div>
                        </div>

                        <div class="col-md-11 clearfix">
                            <input type="checkbox" id="selectAll" />
                             selectAll
                        </div>
                        <hr />
                        <div class="table-responsive">
                            <table class="table table-striped table-bordered">
                                <thead>
                                    <tr>
                                        <th colspan="2" style="width: 10%;">Upload Product</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td><input class="check-one" name='uploadMasters' type="checkbox"
                                                value="product_profiles"></input></td>
                                        <td>Product Profiles</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>

                        <hr />

                        <div class="table-responsive">
                            <table class="table table-striped table-bordered">
                                <thead>
                                    <tr>
                                        <th colspan="2">
                                            <h2>Chennai Depot</h2>
                                        </th>
                                    </tr>
                                    <tr>
                                        <th style="width: 10%;"><input type="checkbox"
                                                id="chennai-selectAll" />&nbsp;&nbsp; Select All</th>
                                        <th>Masters</th>
                                    </tr>
                                </thead>
                                <tbody>

                                    <tr>
                                        <td><input class="chennai-check-one" name='uploadMasters' type="checkbox"
                                                value="account_profiles_chennai"></input></td>
                                        <td>Account Profiles</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                        <hr />

                        <div class="table-responsive">
                            <table class="table table-striped table-bordered">
                                <thead>
                                    <tr>
                                        <th colspan="2">
                                            <h2>Vyasarpadi Depot</h2>
                                        </th>
                                    </tr>
                                    <tr>
                                        <th style="width: 10%;"><input type="checkbox"
                                                id="vysarpadi_selectAll" />&nbsp;&nbsp; Select All</th>
                                        <th>Masters</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td><input class="vysarpadi-check-one" name='uploadMasters' type="checkbox"
                                                value="account_profiles_vysarpadi"></input></td>
                                        <td>Account Profiles</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>

                        <hr />

                        <div class="table-responsive">
                            <table class="table table-striped table-bordered">
                                <thead>
                                    <tr>
                                        <th colspan="2">
                                            <h2>Tambaram Depot</h2>
                                        </th>
                                    </tr>
                                    <tr>
                                        <th style="width: 10%;"><input type="checkbox"
                                                id="thambaram_selectAll" />&nbsp;&nbsp; Select All</th>
                                        <th>Masters</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td><input class="thambaram-check-one" name='uploadMasters' type="checkbox"
                                                value="account_profiles_thambaram"></input></td>
                                        <td>Account Profiles</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                        <hr />


                        <div class="table-responsive">
                            <table class="table table-striped table-bordered">
                                <thead>
                                    <tr>
                                        <th colspan="2">
                                            <h2>Thuraipakkam Depot</h2>
                                        </th>
                                    </tr>
                                    <tr>
                                        <th style="width: 10%;"><input type="checkbox"
                                                id="thuraipkkam_selectAll" />&nbsp;&nbsp; Select All</th>
                                        <th>Masters</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td><input class="thuraipkkam-check-one" name='uploadMasters' type="checkbox"
                                                value="account_profiles_thuraipkkam"></input></td>
                                        <td>Account Profiles</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                        <hr />



                        <div class="table-responsive">
                            <table class="table table-striped table-bordered">
                                <thead>
                                    <tr>
                                        <th colspan="2">
                                            <h2>Pallikkuppam Depot</h2>
                                        </th>
                                    </tr>
                                    <tr>
                                        <th style="width: 10%;"><input type="checkbox"
                                                id="pallikkuppam-selectAll" />&nbsp;&nbsp; Select All</th>
                                        <th>Masters</th>
                                    </tr>
                                </thead>
                                <tbody>

                                    <tr>
                                        <td><input class="pallikkuppam-check-one" name='uploadMasters' type="checkbox"
                                                value="account_profiles_pallikkuppam"></input></td>
                                        <td>Account Profiles</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                        <hr />



                        <!-- Footer -->
                        <jsp:include page="../fragments/m_footer.jsp"></jsp:include>
                    </div>
                </div>

                <spring:url value="/resources/assets/js/joinable.js" var="joinable"></spring:url>
                <script type="text/javascript" src="${joinable}"></script>

                <jsp:include page="../fragments/m_bottom_script.jsp"></jsp:include>

                <spring:url value="/resources/app/upload-uj-chennai.js" var="uploadunclejhonJs"></spring:url>
                <script type="text/javascript" src="${uploadunclejhonJs}"></script>
            </body>

            </html>