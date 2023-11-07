package com.example.taskn6


import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log.d
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import com.example.taskn6.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val emailPattern = "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@" +
            "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
    private var currentUsersNumber = 0
    private var removedUsersNumber = 0
    private lateinit var activeUsersNumberText: AppCompatTextView
    private lateinit var removedUsersNumberText: AppCompatTextView
    private lateinit var firstName: AppCompatEditText
    private lateinit var lastName: AppCompatEditText
    private lateinit var age: AppCompatEditText
    private lateinit var email: AppCompatEditText
    private lateinit var user: User
    private lateinit var userToUpdate: User
    private val users = mutableSetOf<User>()
    private var tryUpdate: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //with
        firstName = binding.firstnameEt
        lastName = binding.lastnameEt
        email = binding.emailEt
        age = binding.ageEt
        activeUsersNumberText = binding.activeUsersNumberTv
        removedUsersNumberText = binding.removedUsersNumberTv

        binding.addUser.setOnClickListener {
            if (fullValidation(firstName, lastName, email, age)) {
                if (!users.contains(user)) {
                    users.add(user)
                    addUser()
                } else {
                    failText("User already exists")
                }
            }
            d("CHECK USERS", users.joinToString())
        }

        binding.removeUser.setOnClickListener {
            if (fullValidation(firstName, lastName, email, age)) {
                if (users.contains(user)) {
                    users.remove(user)
                    removeUser()
                } else {
                    failText("Such user does not exist")
                }
            }
        }
        binding.updateUser.setOnClickListener {
            if (!tryUpdate) {
                if (fullValidation(firstName, lastName, email, age)) {
                    if (users.contains(user)) {
                        updateUser(user)
                    } else {
                        failText("Such user does not exist")
                    }
                }
            } else {
                if (fullValidation(firstName, lastName, email, age)) {
                    if (users.contains(user)) {
                        failText("Such user already exist.")
                    } else {
                        updateUser(user)
                    }
                }
            }
        }
        binding.goBackBtn.setOnClickListener {
            changeVisibility(
                binding.updateUserTv,
                binding.addUser,
                binding.removeUser,
                binding.goBackBtn
            )
            tryUpdate = false
        }
    }

    private fun fullValidation(
        firstName: AppCompatEditText,
        lastName: AppCompatEditText,
        email: AppCompatEditText,
        age: AppCompatEditText
    ): Boolean {
        if (checkIfEmpty(firstName, lastName, email, age)) {
            val ageText: Int? = ageValidation(age)
            val mail: String? = emailValidation(email)

            mail?.let {
                ageText?.let {
                    user = User(
                        firstName.text.toString(),
                        lastName.text.toString(),
                        email.text.toString(),
                        age.text.toString().toInt()
                    )
                    return true
                }
            }
            return false
        }
        return false
    }

    private fun checkIfEmpty(
        firstName: AppCompatEditText,
        lastName: AppCompatEditText,
        email: AppCompatEditText,
        age: AppCompatEditText,
    ): Boolean {
        val fName: String = firstName.text.toString()
        val lName: String = lastName.text.toString()
        val ageN: String = age.text.toString()
        val mail: String = email.text.toString()
        return if (fName.isEmpty() || lName.isEmpty() || ageN.isEmpty() || mail.isEmpty()) {
            showToast(this, "Fill all Fields")
            false
        } else {
            true
        }
    }

    private fun emailValidation(editText: AppCompatEditText): String? {
        val email: String = editText.text.toString()
        return if (!email.matches(emailPattern.toRegex())) {
            showToast(this, "Email is not valid.")
            null
        } else {
            email
        }
    }

    private fun ageValidation(editText: AppCompatEditText): Int? {
        val age: Int? = editText.text.toString().toIntOrNull()
        age?.let {
            if (age < 1 || age > 122) {
                showToast(this, "Age must be positive whole number")
                return null
            } else {
                return age
            }
        }
        showToast(this, "Age must be positive whole number")
        return null
    }

    private fun addUser() {
        currentUsersNumber++
        successText("User was added.")
        activeUsersNumberText.text = "$currentUsersNumber"
        cleanEditTexts()
    }

    private fun removeUser() {
        currentUsersNumber--
        removedUsersNumber++
        successText("User was removed")
        activeUsersNumberText.text = "$currentUsersNumber"
        removedUsersNumberText.text = "$removedUsersNumber"
        cleanEditTexts()
    }

    private fun updateUser(user: User) {
        if (!tryUpdate) {
            changeVisibility(
                binding.updateUserTv,
                binding.addUser,
                binding.removeUser,
                binding.goBackBtn
            )
            successText("")
            userToUpdate = user
            tryUpdate = true
        } else {
            users.remove(userToUpdate)
            users.add(user)
            successText("User updated successfully")
            changeVisibility(
                binding.updateUserTv,
                binding.addUser,
                binding.removeUser,
                binding.goBackBtn
            )
            tryUpdate = false
            cleanEditTexts()
        }
    }

    private fun changeVisibility(vararg views: View) {
        for (view in views) {
            if (view.visibility == View.GONE) {
                view.visibility = View.VISIBLE
            } else {
                view.visibility = View.GONE
            }
        }
    }

    private fun successText(text: String) {
        binding.answerTv.text = text
        binding.answerTv.setTextColor(Color.GREEN)
    }

    private fun failText(text: String) {
        binding.answerTv.text = text
        binding.answerTv.setTextColor(Color.RED)
    }

    private fun cleanEditTexts() {
        firstName.setText("")
        lastName.setText("")
        age.setText("")
        email.setText("")
    }

    private fun showToast(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}