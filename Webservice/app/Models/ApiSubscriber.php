<?php namespace App\Models;

use Illuminate\Contracts\Auth\Authenticatable;
use Illuminate\Database\Eloquent\Collection;
use Illuminate\Database\Eloquent\Model;
use Tymon\JWTAuth\Contracts\JWTSubject;

class ApiSubscriber extends Model implements Authenticatable, JWTSubject
{
    use \Illuminate\Auth\Authenticatable;

    protected $table = 'users';
    protected $primaryKey = 'user_id';

    protected $visible = ['user_id', 'name', 'email', 'password', 'permission', 'report_number', 'created_at', 'updated_at'];
    protected $fillable = ['name', 'email', 'password'];

    /**
     * @param $value
     */
    public function setPasswordAttribute($value)
    {
        $this->attributes['password'] = app('hash')->make($value);
    }

    /**
     * Get the identifier that will be stored in the subject claim of the JWT
     *
     * @return mixed
     */
    public function getJWTIdentifier()
    {
        return $this->getKey();
    }
    /**
     * Return a key value array, containing any custom claims to be added to the JWT
     *
     * @return array
     */
    public function getJWTCustomClaims()
    {
        return [];
    }
}
