<?php namespace App\Models;

use Illuminate\Contracts\Auth\Authenticatable;
use Illuminate\Database\Eloquent\Collection;
use Illuminate\Database\Eloquent\Model;
use Tymon\JWTAuth\Contracts\JWTSubject;

class Report extends Model implements Authenticatable, JWTSubject
{
    use \Illuminate\Auth\Authenticatable;

    protected $table = 'reports';
    protected $primaryKey = 'report_id';

    protected $visible = ['report_id', 'user_id', 'latitude', 'longitude', 'description', 'created_at', 'updated_at'];
    protected $fillable = ['user_id', 'latitude','longitude', 'description'];
    /**
     * @param $value
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
