<?php namespace App\Http\Controllers;

use Dingo\Api\Http\Request;
use Tymon\JWTAuth\JWTAuth;
use Tymon\JWTAuth\Exceptions\JWTException;

class ReportController extends Controller
{
    protected $auth;

    public function __construct(JWTAuth $auth)
    {
        $this->auth = $auth;
    }

    public function getReports(Request $request){
        //
    }

    public function addReport(Request $request){
      //
    }

    public function editReport(Request $request){
      //
    }

    public function deleteReport(Request $request){
      //
    }

}
