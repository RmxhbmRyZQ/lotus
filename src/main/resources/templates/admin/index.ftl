<!DOCTYPE html>
<html>

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="renderer" content="webkit">

    <title>lotus example</title>

    <meta name="keywords" content="">
    <meta name="description" content="">

    <!--[if lt IE 9]>
    <meta http-equiv="refresh" content="0;ie.html"/>
    <![endif]-->

    <link rel="shortcut icon" href="/favicon.ico">
    <link href="/static/assets/css/bootstrap.min.css?v=3.3.6" rel="stylesheet">
    <link href="/static/assets/css/font-awesome.min.css?v=4.4.0" rel="stylesheet">
    <link href="/static/assets/css/animate.css" rel="stylesheet">
    <link href="/static/assets/css/style.css?v=4.1.0" rel="stylesheet">
</head>

<body class="fixed-sidebar full-height-layout gray-bg" style="overflow:hidden">
<div id="wrapper">
    <!--左侧导航开始-->
    <nav class="navbar-default navbar-static-side" role="navigation">
        <div class="nav-close"><i class="fa fa-times-circle"></i>
        </div>
        <div class="sidebar-collapse">
            <ul class="nav" id="side-menu">
                <li class="nav-header">
                    <div class="dropdown profile-element">
                        <a data-toggle="dropdown" class="dropdown-toggle" href="#">
                                <span class="clear">
                                    <span class="block m-t-xs" style="font-size:20px;">
                                        <i class="fa fa-area-chart"></i>
                                        <strong class="font-bold">lotus example</strong>
                                    </span>
                                </span>
                        </a>
                    </div>
                    <div class="logo-element">lotus example</div>
                </li>
                <li class="hidden-folded padder m-t m-b-sm text-muted text-xs">
                    <span class="ng-scope">分类</span>
                </li>
                <li>
                    <a class="J_menuItem" href="/admin/welcome">
                        <i class="fa fa-home"></i>
                        <span class="nav-label">主页</span>
                    </a>
                </li>
                <li>
                    <a href="#">
                        <i class="fa fa fa-cog"></i>
                        <span class="nav-label">系统管理</span>
                        <span class="fa arrow"></span>
                    </a>
                    <ul class="nav nav-second-level">
                        <li>
                            <a class="J_menuItem" href="/admin/user/index">用户管理</a>
                        </li>
                    </ul>
                </li>
                <li class="line dk"></li>
            </ul>
        </div>
    </nav>
    <!--左侧导航结束-->
    <!--右侧部分开始-->
    <div id="page-wrapper" class="gray-bg dashbard-1">
        <div class="row border-bottom">
            <nav class="navbar navbar-static-top" role="navigation" style="margin-bottom: 0">
                <div class="navbar-header"><a class="navbar-minimalize minimalize-styl-2 btn btn-info " href="#"><i
                                class="fa fa-bars"></i> </a>
                </div>
                <ul class="nav navbar-top-links navbar-right">
                    <li class="dropdown">
                        <a class="dropdown-toggle count-info" data-toggle="dropdown" href="#">
                            <i class="fa fa-user"></i> <span
                                    class="label label-primary"></span>【${username}】
                        </a>
                        <ul class="dropdown-menu dropdown-alerts">
                            <li>
                                <a href="/admin/logout">
                                    <div>
                                        <i class="fa fa-remove"></i> 注销
                                        <span class="pull-right text-muted small">${username}</span>
                                    </div>
                                </a>
                            </li>
                        </ul>
                    </li>
                </ul>
            </nav>
        </div>
        <div class="row J_mainContent" id="content-main">
            <iframe id="J_iframe" width="100%" height="100%" src="/admin/welcome" frameborder="0"
                    data-id="index_v1.html" seamless></iframe>
        </div>
    </div>
    <!--右侧部分结束-->
</div>

<!-- 全局js -->
<script src="/static/assets/js/jquery.min.js?v=2.1.4"></script>
<script src="/static/assets/js/bootstrap.min.js?v=3.3.6"></script>
<script src="/static/assets/js/plugins/metisMenu/jquery.metisMenu.js"></script>
<script src="/static/assets/js/plugins/slimscroll/jquery.slimscroll.min.js"></script>
<script src="/static/assets/js/plugins/layer/layer.min.js"></script>

<!-- 自定义js -->
<script src="/static/assets/js/hAdmin.js?v=4.1.0"></script>
<script type="text/javascript" src="/static/assets/js/index.js"></script>
</body>

</html>
