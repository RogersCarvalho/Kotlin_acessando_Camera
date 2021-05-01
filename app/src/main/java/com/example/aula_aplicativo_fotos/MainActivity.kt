package com.example.aula_aplicativo_fotos

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        val PERMISSION_CODE_IMAGE_PICK = 1000
        val IMAGE_PICK_CODE = 1001
        val PERMISSION_CODE_CAMERA = 2000
        var image_uri : Uri? = null
        val OPEN_CAMERA_CODE= 2001
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pick_button.setOnClickListener {
            //Se a versão é maior que no Marchimello precisa pedir permissão
            //Pois a versões iguais ou maiores e Marchimello só liberam com permissão
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //Se a permissão tiver sido negada então entra no IF
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    //Se negada precisa pedir permissão
                    val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permission, PERMISSION_CODE_IMAGE_PICK)
                } else {
                    pickImageFromGalery()
                }
            } else {
                pickImageFromGalery()
            }
        }


         //Trata o botão abrir camera
        open_camera_button.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //Verifica se o acesso a camera e a escrita estão negadas
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                        checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    //Caso tenha sido negada  é preciso solicitar permissão abrindo o alert(allow ou deny)
                    val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    requestPermissions(permission, PERMISSION_CODE_CAMERA)
                } else {
                    openCamera()
                }
            } else {
                openCamera()
            }
        }



    }


    //Trata a permissão solicitada, o evento capta quando o usuário clica em Permitir
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {

            PERMISSION_CODE_IMAGE_PICK -> {
                     if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                     {
                          pickImageFromGalery()
                     }
                else { Toast.makeText(this, "Permissão negada", Toast.LENGTH_LONG) }}

                PERMISSION_CODE_CAMERA -> {
                    if (grantResults.size > 1 &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                            grantResults[1] == PackageManager.PERMISSION_GRANTED)
                    {
                        openCamera()
                    }
                    else { Toast.makeText(this, "Permissão negada", Toast.LENGTH_LONG) }
                }
        }
    }


    //Responsavel por abrir a galeria de fotos
    private fun pickImageFromGalery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }


    //Responsavel por abrir a camera
    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "nova foto")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Imagem capturada")
        //Essa variável é uma estrutura que vai receber uma foto após tirada
        image_uri= contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, OPEN_CAMERA_CODE)
    }


       //Evento chamado quando finaliza a camera ou a imagem
       //O CODE define se é camera ou imagem para jogar no image_view
        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
                image_view.setImageURI(data?.data)
            }
        //Acrescenta a foto tirada na image_view
        if (resultCode == Activity.RESULT_OK && requestCode == OPEN_CAMERA_CODE) {
            image_view.setImageURI(image_uri)
        }
    }


}
