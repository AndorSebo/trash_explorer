<?php namespace App\Http\Controllers;

use Dingo\Api\Http\Request;
use Tymon\JWTAuth\JWTAuth;
use Tymon\JWTAuth\Exceptions\JWTException;
use App\Models\ApiSubscriber;

class AuthenticateController extends Controller
{
    protected $auth;

    public function __construct(JWTAuth $auth)
    {
        $this->auth = $auth;
    }

    public function backend(Request $request)
    {
        // grab credentials from the request
        $credentials = $request->only('email', 'password');

        try {
            // attempt to verify the credentials and create a token for the user
            if (! $token = $this->auth->attempt($credentials)) {
                return $this->Datareturn(false, 411, [], "invalid_credentials");
            }
        } catch (JWTException $e) {
            // something went wrong whilst attempting to encode the token
            return response()->json(['error' => 'could_not_create_token'], 500);
        }

        // all good so return the token
        $userid = $this->auth->user()->user_id;        
        return $this->LoginDatareturn(true, 200, compact('token'), $userid, "success_signin");
    }

    public function signUp(Request $request){
      $name = $request->name;
      $email = $request->email;
      $password = $request->password;
      $repassword = $request->repassword;

      if(count(ApiSubscriber::where('name', $name)->get()) > 0){
        return $this->Datareturn(false, 401, [], "name_reserved");
      }
      if(count(ApiSubscriber::where('email', $email)->get()) > 0){
        return $this->Datareturn(false, 401, [], "email_reserved");
      }
      if ($email == "" || $password == "" || $name == "" || $repassword == ""){
        return $this->Datareturn(false, 401, [], "same_parameter_empty");
      }
      if(strlen($password) < 6){
        return $this->Datareturn(false, 401, [], "password_to_short");
      }
      if(!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        return $this->Datareturn(false, 401, [], "its_not_email_format");
      }
      if(preg_match('/[\'^£$%&*()}{@#~?><>,!|=_+¬-]/', $name) || preg_match('/[\'^£$%&*()!}{@#~?><>,|=_+¬-]/', $password)){
        return $this->Datareturn(false, 401, [], "special_caracter_on_name_or_password");
      }
      if(strlen(trim($name)) == 0 || strlen(trim($password)) == 0){
        return $this->Datareturn(false, 401, [], "incorrect_name_or_password");
      }
      if($repassword != $password){
        return $this->Datareturn(false, 401, [], "password_and_repassword_are_not_match");
      }

      ApiSubscriber::create([
          'name' => $name,
          'email' => $email,
          'password' => $password,
          'report_number' => 0,
          'permission' => 0,
      ]);

      return $this->Datareturn(true, 200, [], "success_signup");
    }
}
