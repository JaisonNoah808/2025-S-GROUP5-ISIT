package com.example.ingrediscan.ui.createAccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ingrediscan.R
import com.example.ingrediscan.databinding.FragmentCreateAccountBinding

class CreateAccountFragment : Fragment() {

    private var _binding: FragmentCreateAccountBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val createAccountViewModel =
            ViewModelProvider(this).get(CreateAccountViewModel::class.java)

        _binding = FragmentCreateAccountBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // sign up button
        binding.buttonSignup.setOnClickListener {
            val fullname = binding.enterFullname.text.toString().trim()
            val username = binding.enterUsername.text.toString().trim()
            val password = binding.enterPassword.text.toString().trim()
            val confirmPassword = binding.confirmPassword.text.toString().trim()

            // TO DO: checking error and valid input

            // successful sign up
            findNavController().navigate(R.id.signup_to_home)
        }

        // go back to login page if already have an account
        binding.Linksignin.setOnClickListener {
            findNavController().navigate(R.id.signup_to_login)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
