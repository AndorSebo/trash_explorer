<?php

/*
|--------------------------------------------------------------------------
| Application Routes
|--------------------------------------------------------------------------
|
| Here is where you can register all of the routes for an application.
| It is a breeze. Simply tell Lumen the URIs it should respond to
| and give it the Closure to call when that URI is requested.
|
*/

$api = app('Dingo\Api\Routing\Router');

// JWT Protected routes
$api->version('v1', ['middleware' => 'api.auth', 'providers' => 'jwt'], function ($api) {
    //$api->get('/index', 'App\Http\Controllers\BackendController@index');
    $api->get('/profile', 'App\Http\Controllers\AuthenticateController@getUser');
    $api->post('/passwordchange', 'App\Http\Controllers\AuthenticateController@passwordChange');
    $api->post('/newreport', 'App\Http\Controllers\ReportController@newReport');
    $api->get('/getalluser', 'App\Http\Controllers\AuthenticateController@getAllUser');
    $api->get('/getreport', 'App\Http\Controllers\ReportController@getReport');
    $api->post('/deletereport', 'App\Http\Controllers\ReportController@deleteReport');
});

// Publicly accessible routes
$api->version('v1', [], function ($api) {
    $api->post('/signin', 'App\Http\Controllers\AuthenticateController@backend');
    $api->post('/signup', 'App\Http\Controllers\AuthenticateController@signUp');
});
