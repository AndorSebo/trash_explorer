<?php namespace App\Http\Controllers;

use Dingo\Api\Http\Request;
use Tymon\JWTAuth\JWTAuth;
use Tymon\JWTAuth\Exceptions\JWTException;

class ImageController extends Controller
{
    protected $auth;

    public function __construct(JWTAuth $auth)
    {
        $this->auth = $auth;
    }

    public function getImages(Request $request){
        //
    }

    public function addImage(Request $request){
      //
    }

    public function editImage(Request $request){
      //
    }

    public function deleteImage(Request $request){
      //
    }

}
